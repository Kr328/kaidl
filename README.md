# kaidl

Generate [AIDL](https://developer.android.com/guide/components/aidl)-like android binder interface with **Kotlin**



### Available Types

- Primitives

  - `Int` 
  - `Long`
  - `Boolean`
  - `Float`
  - `Double`
  - `String`
  - `Byte`
  - `Char`

- Primitive Arrays

  - `BooleanArray`
  - `ByteArray`
  - `CharArray`
  - `DoubleArray`
  - `FloatArray`
  - `IntArray`
  - `LongArray`
  - `SparseBooleanArray`

- Containers with Generic

  - `List<T>`
  - `Array<T>`
  - `Map<K, V>`
  - `Set<T>`

- Parcelables

  - Custom `Parcelable`
  - `Bundle`
  
- Active Objects
  
  - `Binder`
  - Other kaidl interfaces
  
  

### Usage

- Add 'KSP' to your project

  + Add ksp plugin repository in your project's `setting.gradle(.kts)`

     ```kotlin
     pluginManagement {
         repositories {
             gradlePluginPortal()
             google()
         }
     }
     ```

  + Apply plugin `kotlin-processing`
   
     ```kotlin
     plugins {
         id("com.google.devtools.ksp") version "1.5.0-1.0.0-alpha10"
         // ...other plugins
     }
     ```

- Add 'Kaidl' to your project

  + Add 'kaidl' repositories

     ```kotlin
     repositories {
         // ... other repositories
   
         maven {
             url = uri("https://maven.kr328.app")
         }
     }
     ```

  + Add 'ksp' and runtime dependencies

    ```kotlin
     dependencies {
         ksp("com.github.kr328.kaidl:kaidl:1.11")

         implementation("com.github.kr328.kaidl:kaidl-runtime:1.11")

         // ...other dependencies
     }
     ```

- Example

  See also [test module](https://github.com/Kr328/kaidl/tree/main/test)

- Make IDE Aware Of Generated Code

  See also [ksp](https://github.com/google/ksp#make-ide-aware-of-generated-code)

### Credit

- [Kotlin Symbol Processing](https://github.com/google/ksp)
- [Kotlinpoet](https://github.com/square/kotlinpoet)