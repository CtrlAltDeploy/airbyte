/*
 * Copyright (c) 2025 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.integrations.destination.bigquery

import io.airbyte.cdk.load.check.CheckIntegrationTest
import io.airbyte.cdk.load.check.CheckTestConfig
import io.airbyte.integrations.destination.bigquery.spec.BigquerySpecification
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class BigQueryCheckTest :
    CheckIntegrationTest<BigquerySpecification>(
        successConfigFilenames =
            listOf(
                CheckTestConfig(
                    Files.readString(
                        Path.of("secrets/credentials-1s1t-disabletd-gcs-raw-override.json")
                    )
                ),
            ),
        // TODO we maybe should add some configs that are expected to fail `check`
        failConfigFilenamesAndFailureReasons = mapOf(),
        additionalMicronautEnvs = additionalMicronautEnvs,
    ) {
    @Test
    fun testMyTest() {
        for (tc in successConfigFilenames) {
            val updatedConfig = updateConfig(tc.configContents)
            val process =
                destinationProcessFactory.createDestinationProcess(
                    "check",
                    configContents = updatedConfig,
                    featureFlags = tc.featureFlags.toTypedArray(),
                    micronautProperties = micronautProperties,
                )
            runBlocking { process.run() }
        }
    }
}
