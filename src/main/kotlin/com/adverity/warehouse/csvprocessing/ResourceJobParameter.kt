package com.adverity.warehouse.csvprocessing

import org.springframework.batch.core.JobParameter
import org.springframework.core.io.Resource

internal class ResourceJobParameter<T : Resource>(private val customParameter: T)
    : JobParameter("", false) {

    override fun getValue(): T {
        return customParameter
    }

}