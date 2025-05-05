/*
 * Copyright (c) 2025 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.integrations.destination.bigquery

import io.airbyte.cdk.load.check.DestinationChecker
import io.airbyte.integrations.destination.bigquery.spec.BigqueryConfiguration
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Named("clientProvidedChecker")
class BigqueryChecker : DestinationChecker<BigqueryConfiguration> {
    override fun check(config: BigqueryConfiguration) {
        println("Do I go here?")
    }
}
