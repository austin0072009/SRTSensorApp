package threeDvector
//矩阵和向量相关操作
import java.lang.IllegalArgumentException
import java.lang.Math.pow
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.sin
import kotlin.math.sqrt

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
    if (A.size != B.size) throw IllegalArgumentException("The two vectors must be identical.")
    for (i in 0 until A.size)
        result += A[i] * B[i]
    return result
}

fun MiddleAngle(Ort0: Vec3D, Ort1: Vec3D, a: Double) = Vec3D(DoubleArray(3) { i -> sin(asin(Ort0[i]) * (1 - a) + asin(Ort1[i]) * a) })
fun Rotate(X: Vec3D, Angle: Vec3D): Vec3D {}


