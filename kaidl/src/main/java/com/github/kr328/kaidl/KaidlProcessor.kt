package com.github.kr328.kaidl

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
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
        resolver.getSymbolsWithAnnotation(INTERFACE.canonicalName)
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.INTERFACE }
            .forEach(this::generate)
    }

    private fun generate(classDeclaration: KSClassDeclaration) {
        val className = classDeclaration.toClassName()
        val methods = classDeclaration.parseMethods()

        codeGenerator.createNewFile(className.packageName, className.simpleName).writer().use {
            FileSpec.builder(className.packageName, "")
                .addComment("Generated for $className")
                .generateStub(className, methods)
                .generateProxy(className, methods)
                .generateWrap(className)
                .generateUnwrap(className)
                .build()
                .writeTo(it)
        }
    }
}