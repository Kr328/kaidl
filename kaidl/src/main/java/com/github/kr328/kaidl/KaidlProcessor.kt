package com.github.kr328.kaidl

import com.github.kr328.kaidl.resolver.INTERFACE
import com.github.kr328.kaidl.resolver.resolveFunctions
import com.github.kr328.kaidl.resolver.store
import com.github.kr328.kaidl.resolver.toClassName
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec

class KaidlProcessor(private val codeGenerator: CodeGenerator) : SymbolProcessor {
    override fun finish() {

    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        return resolver.store {
            val classes = resolver.getSymbolsWithAnnotation(INTERFACE.canonicalName)
                .filterIsInstance<KSClassDeclaration>()
                .toList()

            val result = classes.filter { !it.validate() }

            classes.forEach {
                require(it.classKind == ClassKind.INTERFACE) {
                    throw IllegalArgumentException("@BinderInterface support only interfaces")
                }

                if (it.validate()) {
                    generate(it)
                }
            }

            result
        }
    }

    private fun generate(classDeclaration: KSClassDeclaration) {
        val className = classDeclaration.toClassName()
        val functions = classDeclaration.resolveFunctions()
        val dependencies = Dependencies(true, classDeclaration.containingFile!!)

        codeGenerator.createNewFile(dependencies, className.packageName, className.simpleName)
            .writer().use {
            FileSpec.builder(className.packageName, "")
                .addComment("Generated for $className")
                .addAnnotation(
                    AnnotationSpec.builder(Suppress::class)
                        .addMember(DEFAULT_SUPPRESS.joinToString(", ") { s -> "\"$s\"" })
                        .build()
                )
                .addStub(className, functions)
                .addProxyClass(className, functions)
                .addWrap(className)
                .addUnwrap(className)
                .build()
                .writeTo(it)
        }
    }

    companion object {
        private val DEFAULT_SUPPRESS = arrayOf(
            "NAME_SHADOWING",
            "UNUSED_VARIABLE",
            "UNNECESSARY_NOT_NULL_ASSERTION",
            "UNUSED_PARAMETER",
        )
    }
}