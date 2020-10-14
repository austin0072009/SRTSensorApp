package threeDvector
//矩阵和向量相关操作
import androidx.core.graphics.rotationMatrix
import java.lang.IllegalArgumentException
import java.lang.Math.pow
import kotlin.math.*

class Matrix3D(val elements: DoubleArray = DoubleArray(9), var transposed: Boolean = false) {
    constructor(M: Matrix3D) : this(M.elements.clone(), M.transposed)

    val trace get() = this[1, 1] + this[2, 2] + this[3, 3]
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
                result[i, j] = this[i, 1] * other[1, j] + this[i, 2] * other[2, j] + this[i, 3] * other[3, j]
            }
        return result
    }

    fun toQuaternion(): Quaternion {
        val r = sqrt(1 + trace)
        val s = 1 / (2 * r)
        return Quaternion((this[3, 2] - this[2, 3]) * s, (this[1, 3] - this[3, 1]) * s, (this[2, 1] - this[1, 2]) * s)
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

//旋转的四元数表示
class Quaternion(val qx: Double, val qy: Double, val qz: Double) {
    constructor(Ort: Vec3D) : this(Ort.x, Ort.y, Ort.z)

    val magnitude by lazy { sqrt(qx * qx + qy * qy + qz * qz) }
    val qw by lazy { sqrt(1 - qx * qx - qy * qy - qz * qz) }
    fun toAxisAngle() = AxisAngle(2 * acos(qw), qx / magnitude, qy / magnitude, qz / magnitude)
    fun toRotationMatrix(): Matrix3D {
        val result = Matrix3D()
        result[0, 0] = 1 - 2 * qy * qy - 2 * qz * qz
        result[0, 1] = 2 * qx * qy - 2 * qz * qw
        result[0, 2] = 2 * qx * qz + 2 * qy * qw
        result[1, 0] = 2 * qx * qy + 2 * qz * qw
        result[1, 1] = 1 - 2 * qx * qx - 2 * qz * qz
        result[1, 2] = 2 * qy * qz - 2 * qx * qw
        result[2, 0] = 2 * qx * qz - 2 * qy * qw
        result[2, 1] = 2 * qy * qz + 2 * qx * qw
        result[2, 2] = 1 - 2 * qx * qx - 2 * qy * qy
        return result
    }
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


inline fun MiddleAngle(Ort0: Vec3D, Ort1: Vec3D, a: Double): Matrix3D {
    val R0 = Quaternion(Ort0).toRotationMatrix()
    val R1 = Quaternion(Ort1).toRotationMatrix()
    return ((R1 * R0.transpose).toQuaternion().toAxisAngle() * a).toRotationMatrix() * R0
}

inline fun Vec3D.Rotate(R: Matrix3D) = R * this


