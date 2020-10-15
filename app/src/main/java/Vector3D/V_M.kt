package threeDvector
//矩阵和向量相关操作
import threeDvector.QuaternionMode.NOTHING
import threeDvector.QuaternionMode.UNIT
import java.lang.Math.pow
import kotlin.math.*

class Matrix3D(val elements: DoubleArray = DoubleArray(9), var transposed: Boolean = false) {
    constructor(M: Matrix3D) : this(M.elements.clone(), M.transposed)

    val trace get() = this[0, 0] + this[1, 1] + this[2, 2]
    fun transposed() {
        transposed = !transposed
    }

    val transpose get() = Matrix3D(elements.clone(), !transposed)

    operator fun get(Row: Int, Col: Int): Double = when (transposed) {
        false -> elements[Col * 3 + Row]
        true -> elements[Row * 3 + Col]
    }

    operator fun set(Row: Int, Col: Int, value: Double): Unit = when (transposed) {
        false -> elements[Col * 3 + Row] = value
        true -> elements[Row * 3 + Col] = value
    }

    operator fun set(Row: Int, Col: Int, value: Number): Unit = set(Row, Col, value.toDouble())
    operator fun times(other: Vec3D): Vec3D {
        val result = Vec3D()
        for (i in 0 until 3)
            for (k in 0 until 3)
                result[i] += this[i, k] * other[k]
        return result
    }

    operator fun times(other: Matrix3D): Matrix3D {
        val result = Matrix3D()
        for (i in 0 until 3)
            for (j in 0 until 3) {
                result[i, j] = this[i, 0] * other[0, j] + this[i, 1] * other[1, j] + this[i, 2] * other[2, j]
            }
        return result
    }

    fun toQuaternion(): Quaternion {
        val r = sqrt(1 + trace)
        val s = 1 / (2 * r)
        return Quaternion((this[2, 1] - this[1, 2]) * s, (this[0, 2] - this[2, 0]) * s, (this[1, 0] - this[0, 1]) * s)
    }
}


//支持[]，范数，点积
interface VecTor {
    val size: Int //get() = vector.size
    operator fun get(index: Int): Double //= vector[index]
    operator fun set(index: Int, value: Double) /*{
        vector[index] = value
    }*/
    fun norm(p: Any = 2): Double {
        var result = 0.0
        for (i in 0 until size)
            when (p) {
                0, 0.0 -> result += if (this[i] == 0.0) 0 else 1
                1 -> result += abs(this[i])
                2 -> result += this[i] * this[i]
                is Double -> result += pow(abs(this[i]), p)
                "Infinity" -> if (abs(this[i]) > result) result = abs(this[i])
            }
        return when (p) {
            0, 0.0, 1, "Infinity" -> result
            2 -> sqrt(result)
            is Double -> pow(result, 1 / p)
            else -> throw IllegalArgumentException("P must be number or Infinity")
        }
    }
}

fun dot(A: VecTor, B: VecTor): Double {
    var result: Double = 0.0
    if (A.size == B.size) {
        for (i in 0 until A.size)
            result += A[i] * B[i]
        return result
    } else throw IllegalArgumentException("The two vectors must be identical.")
}

enum class QuaternionMode { NOTHING, UNIT }

//旋转的四元数表示
class Quaternion {
    val x: Double
    val y: Double
    val z: Double
    val w: Double
    val unitized: Boolean

    //仅用于内部运算结果
    private constructor(x: Double, y: Double, z: Double, w: Double, unitized: Boolean) {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
        this.unitized = unitized
    }

    constructor(x: Double, y: Double, z: Double, w: Double, QMode: QuaternionMode = NOTHING) {
        when (QMode) {
            NOTHING -> {
                this.x = x
                this.y = y
                this.z = z
                this.w = w
                this.unitized = false
            }
            UNIT -> {
                val len = sqrt(x * x + y * y + z * z + w * w)
                this.x = x / len
                this.y = y / len
                this.z = z / len
                this.w = w / len
                this.unitized = true
            }
        }
    }

    constructor(x: Double, y: Double, z: Double, QMode: QuaternionMode = UNIT) {
        this.x = x
        this.y = y
        this.z = z
        when (QMode) {
            NOTHING -> {
                this.w = 0.0
                this.unitized = false
            }
            UNIT -> {
                this.w = sqrt(1 - x * x - y * y - z * z)
                this.unitized = true
            }
        }
    }

    constructor(Ort: Vec3D, QMode: QuaternionMode = UNIT) : this(Ort.x, Ort.y, Ort.z, QMode)

    //目前进行这两个函数是默认归一化的
    fun toAxisAngle(): AxisAngle {
        val magnitude = sqrt(x * x + y * y + z * z)
        return AxisAngle(2 * acos(w), x / magnitude, y / magnitude, z / magnitude)
    }

    fun toRotationMatrix(): Matrix3D {
        val result = Matrix3D()
        result[0, 0] = 1 - 2 * y * y - 2 * z * z
        result[0, 1] = 2 * x * y - 2 * z * w
        result[0, 2] = 2 * x * z + 2 * y * w
        result[1, 0] = 2 * x * y + 2 * z * w
        result[1, 1] = 1 - 2 * x * x - 2 * z * z
        result[1, 2] = 2 * y * z - 2 * x * w
        result[2, 0] = 2 * x * z - 2 * y * w
        result[2, 1] = 2 * y * z + 2 * x * w
        result[2, 2] = 1 - 2 * x * x - 2 * y * y
        return result
    }

    inline val vec get() = Vec3D(x,y,z)
    val conj get() = Quaternion(-x, -y, -z, w, unitized)
    inline val Unit get() = Quaternion(x, y, z, w, UNIT)
    operator fun plus(other: Quaternion) = Quaternion(x + other.x, y + other.y, z + other.z, w + other.w)
    operator fun times(other: Quaternion) = Quaternion(
            w * other.x + x * other.w + y * other.z - z * other.y,
            w * other.y + y * other.w + z * other.x - x * other.z,
            w * other.z + z * other.w + x * other.y - y * other.x,
            w * other.w - x * other.x - y * other.y - z * other.z,
            unitized && other.unitized
    )

    operator fun unaryMinus() = Quaternion(-x, -y, -z, -w, unitized)
    operator fun times(other: Double) = Quaternion(x * other, y * other, z * other, w * other)
}

//用旋转轴和旋转角表示
class AxisAngle(val angle: Double, val x: Double, val y: Double, val z: Double) {
    operator fun times(other: Double) = AxisAngle(angle * other, x, y, z)
    fun toQuaternion() = Quaternion(x * sin(angle / 2), y * sin(angle / 2), z * sin(angle / 2))
    fun toRotationMatrix(): Matrix3D {
        val result = Matrix3D()
        result[0, 0] = cos(angle) + x * x * (1 - cos(angle))
        result[0, 1] = -z * sin(angle) + x * y * (1 - cos(angle))
        result[0, 2] = y * sin(angle) + x * z * (1 - cos(angle))
        result[1, 0] = z * sin(angle) + x * y * (1 - cos(angle))
        result[1, 1] = cos(angle) + y * y * (1 - cos(angle))
        result[1, 2] = -x * sin(angle) + y * z * (1 - cos(angle))
        result[2, 0] = -y * sin(angle) + x * z * (1 - cos(angle))
        result[2, 1] = x * sin(angle) + y * z * (1 - cos(angle))
        result[2, 2] = cos(angle) + z * z * (1 - cos(angle))
        return result
    }
}

//旋转的线性插值
fun Slerp(Ort0: Vec3D, Ort1: Vec3D, t: Double): Quaternion {
    val R0 = Quaternion(Ort0)
    var R1 = Quaternion(Ort1)
    var cosa = R0.w * R1.w + R0.x * R1.x + R0.y * R1.y + R0.z * R1.z
    if (cosa < 0.0) {
        R1 = -R1
        cosa = -cosa
    }
    val k0:Double
    val k1:Double
    if (cosa > 0.9995) {
        k0 = 1.0 - t
        k1 = t
    } else {
        val sina = sqrt(1.0 - cosa * cosa)
        val a = atan2(sina, cosa)
        k0 = sin((1.0 - t) * a) / sina
        k1 = sin(t * a) / sina
    }
    return (R0 * k0 + R1 * k1).Unit
}

//旋转 p‘=qpq^-1=qpq*
fun Vec3D.Rotate(Q: Quaternion) = (Q * Quaternion(this, NOTHING) * Q.conj).vec


