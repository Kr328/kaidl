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

See also [test](test/)



### Credit

- [Kotlin Symbol Processing](https://github.com/google/ksp)
- [Kotlinpoet](https://github.com/square/kotlinpoet)