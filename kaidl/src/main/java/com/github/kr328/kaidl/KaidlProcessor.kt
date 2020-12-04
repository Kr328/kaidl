package com.github.kr328.kaidl

import com.github.kr328.kaidl.resolver.resolveFunctions
import com.github.kr328.kaidl.resolver.store
import com.github.kr328.kaidl.resolver.toClassName
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec

class KaidlProcessor : SymbolProcessor {
    private lateinit var codeGenerator: CodeGenerator

    override fun finish() {

    }

    override fun init(
        options: Map<String, String>,
        kotlinVersion: KotlinVersion,
        codeGenerator: CodeGenerator,
        logger: KSPLogger
    ) {
        this.codeGenerator = codeGenerator
    }

    override fun process(resolver: Resolver) {
        resolver.store {
            resolver.getSymbolsWithAnnotation(INTERFACE.canonicalName)
                .filterIsInstance<KSClassDeclaration>()
                .forEach {
                    require(it.classKind == ClassKind.INTERFACE) {
                        throw IllegalArgumentException("@BinderInterface support only interfaces")
                    }

                    generate(it)
                }
        }
    }

    private fun generate(classDeclaration: KSClassDeclaration) {
        val className = classDeclaration.toClassName()
        val functions = classDeclaration.resolveFunctions()

        codeGenerator.createNewFile(className.packageName, className.simpleName).writer().use {
            FileSpec.builder(className.packageName, "")
                .addComment("Generated for $className")
                .addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S, %S", "UNUSED", "NAME_SHADOWING").build())
                .addStub(className, functions)
                .addProxyClass(className, functions)
                .addWrap(className)
                .addUnwrap(className)
                .build()
                .writeTo(it)
        }
    }
}