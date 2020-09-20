package com.adverity.warehouse.mapper

internal interface Mapper<S, T> {

    fun map(source: S): T

}