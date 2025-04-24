/*
 * Copyright (c) 2025 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.integrations.destination.bigquery.write

import com.google.cloud.bigquery.BigQuery
import io.airbyte.cdk.load.orchestration.db.direct_load_table.DefaultDirectLoadTableSqlOperations
import io.airbyte.cdk.load.orchestration.db.direct_load_table.DirectLoadTableExecutionConfig
import io.airbyte.cdk.load.orchestration.db.direct_load_table.DirectLoadTableWriter
import io.airbyte.cdk.load.orchestration.db.direct_load_table.migrations.DefaultDirectLoadTableTempTableNameMigration
import io.airbyte.cdk.load.orchestration.db.legacy_typing_deduping.TableCatalog
import io.airbyte.cdk.load.write.StreamStateStore
import io.airbyte.integrations.destination.bigquery.spec.BigqueryConfiguration
import io.airbyte.integrations.destination.bigquery.typing_deduping.BigQueryDatabaseHandler
import io.airbyte.integrations.destination.bigquery.typing_deduping.BigQuerySqlGenerator
import io.airbyte.integrations.destination.bigquery.typing_deduping.BigqueryDatabaseInitialStatusGatherer
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton

@Factory
class BigqueryWriterFactory(
    private val bigquery: BigQuery,
    private val config: BigqueryConfiguration,
    private val names: TableCatalog,
    private val streamStateStore: StreamStateStore<DirectLoadTableExecutionConfig>,
) {
    @Singleton
    fun make(): DirectLoadTableWriter {
        val destinationHandler = BigQueryDatabaseHandler(bigquery, config.datasetLocation.region)
        val sqlTableOperations =
            DefaultDirectLoadTableSqlOperations(
                BigQuerySqlGenerator(config.projectId, config.datasetLocation.region),
                destinationHandler,
            )
        return DirectLoadTableWriter(
            names,
            BigqueryDatabaseInitialStatusGatherer(bigquery),
            destinationHandler,
            BigqueryDirectLoadTableNativeOperations(bigquery),
            sqlTableOperations,
            streamStateStore,
            DefaultDirectLoadTableTempTableNameMigration(
                BigqueryDirectLoadTableExistenceChecker(bigquery),
                sqlTableOperations,
            )
        )
    }
}
