package threeDvector

import java.lang.IllegalArgumentException
import kotlin.math.sqrt


data class Vec3D(var x: Double, var y: Double, var z: Double) {
    constructor(coord: Array<Number>) : this(coord[0].toDouble(), coord[1].toDouble(), coord[2].toDouble())
    constructor(x: Number, y: Number, z: Number) : this(x.toDouble(), y.toDouble(), z.toDouble())

    companion object {
        val UNIT_X = Vec3D(1, 0, 0)
        val UNIT_Y = Vec3D(0, 1, 0)
        val UNIT_Z = Vec3D(0, 0, 1)
    }

    operator fun get(index: Int) = when (index) {
        0 -> x
        1 -> y
        2 -> z
        else -> throw IllegalArgumentException("It is a 3D vector.")
    }

    operator fun set(index: Int, value: Number): Unit = when (index) {
        0 -> x = value.toDouble()
        1 -> y = value.toDouble()
        2 -> z = value.toDouble()
        else -> throw IllegalArgumentException("It is a 3D vector.")
    }

    //cross product
    operator fun times(other: Vec3D) = Vec3D(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)

    val magnitude get() = sqrt(x * x + y * y + z * z)
    val size get() = 3
}