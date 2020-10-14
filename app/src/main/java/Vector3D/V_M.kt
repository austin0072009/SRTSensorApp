package threeDvector
//矩阵和向量相关操作
import androidx.core.graphics.rotationMatrix
import java.lang.IllegalArgumentException
import java.lang.Math.pow
import kotlin.math.*

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
}

//用旋转轴和旋转角表示
class AxisAngle(val angle: Double, val x: Double, val y: Double, val z: Double) {
    operator fun times(other: Double) = AxisAngle(angle * other, x, y, z)
    fun toQuaternion() = Quaternion(x * sin(angle / 2), y * sin(angle / 2), z * sin(angle / 2))
}


fun MiddleAngle(Ort0: Vec3D, Ort1: Vec3D, a: Double): AxisAngle
fun Rotate(X: Vec3D, Angle: AxisAngle): Vec3D {}


