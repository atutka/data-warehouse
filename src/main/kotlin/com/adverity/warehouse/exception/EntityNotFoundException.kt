package com.adverity.warehouse.exception

internal class EntityNotFoundException(msg: String) : IllegalArgumentException(msg)

internal class MetricCannotBeGroupedException : IllegalArgumentException("Grouping is allowed only for calculation metrics")