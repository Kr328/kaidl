package com.github.kr328.kaidl.test

import android.os.IBinder

class LoopbackIBinder(impl: IBinder) : IBinder by impl