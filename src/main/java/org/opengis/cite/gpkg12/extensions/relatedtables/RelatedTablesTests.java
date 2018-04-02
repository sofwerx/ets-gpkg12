package org.opengis.cite.gpkg12.extensions.relatedtables;

import java.sql.PreparedStatement;
import static org.testng.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.opengis.cite.gpkg12.CommonFixture;
import org.opengis.cite.gpkg12.ErrorMessage;
import org.opengis.cite.gpkg12.ErrorMessageKeys;
import org.opengis.cite.gpkg12.util.DatabaseUtility;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Defines test methods that apply to the GeoPackage Related Tables extension.
 *
 * <p style="margin-bottom: 0.5em">
 * <strong>Sources</strong>
 * </p>
 * <ul>
 * <li><a href="TODO" target= "_blank">
 * OGC GeoPackage Related Tables Extension (DRAFT)</a> (OGC 18-000)</li>
 * </ul>
 *
 * @author Brad Hards
 */
public class RelatedTablesTests extends CommonFixture {

    /**
     * Test case {@code /conf/table-defs/applicability}
     *
     * Verify whether the Related Tables Extension is applicable.
     *
     * @param testContext test context provided by calling test harness.
     *
     * @throws SQLException If an SQL query causes an error
     */
    @BeforeClass
    public void activeExtension(ITestContext testContext) throws SQLException {
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkg_extensions"), ErrorMessage.format(ErrorMessageKeys.CONFORMANCE_CLASS_NOT_USED, "Related Tables Extension"));
        try (
                final Statement statement = this.databaseConnection.createStatement();
                final ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM gpkg_extensions WHERE extension_name IN ('related_tables', 'gpkg_related_tables')");) {
            resultSet.next();
            assertTrue(resultSet.getInt(1) != 0, ErrorMessage.format(ErrorMessageKeys.CONFORMANCE_CLASS_NOT_USED, "Related Tables Extension"));
        }
    }

    /**
     * Test case {@code /conf/table-defs/ger}
     *
     * Verify that the gpkgext_relations table is listed in the gpkg_extensions
     * table
     *
     * @throws SQLException If an SQL query causes an error
     */
    @Test(description = "See OGC 18-000: Requirement 1")
    public void relations_table_in_extensions() throws SQLException {
        try (
                final Statement statement = this.databaseConnection.createStatement();
                final ResultSet resultSet = statement.executeQuery("SELECT * FROM gpkg_extensions WHERE table_name = 'gpkgext_relations'");) {
            int passFlag = 0;
            final long flagMask = 0b1111;
            if (resultSet.next()) {
                if (resultSet.getObject("column_name") == null) {
                    passFlag |= 1;
                }
                if ("related_tables".equals(resultSet.getString("extension_name")) || ("gpkg_related_tables".equals(resultSet.getString("extension_name")))) {
                    passFlag |= (1 << 1);
                }
                if ("TBD".equals(resultSet.getString("definition"))) {
                    passFlag |= (1 << 2);
                }
                if ("read-write".equals(resultSet.getString("scope"))) {
                    passFlag |= (1 << 3);
                }
                assertTrue((passFlag & flagMask) == flagMask, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_EXTENSION_ROW_MISSING, String.format("gpkgext_relations - missing row flag %d", passFlag)));
            } else {
                Assert.fail(ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_EXTENSION_ROW_MISSING, "gpkgext_relations"));
            }
        }
    }

    /**
     * Test case {@code /conf/table-defs/extensions-gerr}
     *
     * Verify that the gpkg_extensions table contains at least one user defined
     * mapping table.
     *
     * @throws SQLException If an SQL query causes an error
     */
    @Test(description = "See OGC 18-000: Requirement 2")
    public void mapping_tables_in_extensions() throws SQLException {
        try (
                final Statement statement = this.databaseConnection.createStatement();
                final ResultSet resultSet = statement.executeQuery("SELECT table_name FROM gpkg_extensions WHERE (extension_name IN ('related_tables', 'gpkg_related_tables') AND table_name != 'gpkgext_relations')");) {
            int numRows = 0;
            while (resultSet.next()) {
                numRows++;
                final String table_name = resultSet.getString("table_name");
                assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, table_name), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, table_name));
            }
            if (numRows == 0) {
                Assert.fail(ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_EXTENSION_ROW_MISSING, "at least one mapping table row"));
            }
        }
    }

    /**
     * Test case {@code /conf/table-defs/extensions-udmt}
     *
     * Verify that the gpkg_extensions table contains the required user defined
     * mapping table contents.
     *
     * @throws SQLException If an SQL query causes an error
     */
    @Test(description = "See OGC 18-000: Requirement 3")
    public void extension_table_rows() throws SQLException {
        try (
                final Statement statement = this.databaseConnection.createStatement();
                final ResultSet resultSet = statement.executeQuery("SELECT table_name, column_name, definition, scope FROM gpkg_extensions WHERE (extension_name IN ('related_tables', 'gpkg_related_tables') AND table_name != 'gpkgext_relations')");) {

            int numRows = 0;
            while (resultSet.next()) {
                numRows++;
                final String name = resultSet.getString("table_name");
                int passFlag = 0;
                final long flagMask = 0b111;
                if (resultSet.getObject("column_name") == null) {
                    passFlag |= 1;
                }
                if ("TBD".equals(resultSet.getString("definition"))) {
                    passFlag |= (1 << 1);
                }
                if ("read-write".equals(resultSet.getString("scope"))) {
                    passFlag |= (1 << 2);
                }
                assertTrue((passFlag & flagMask) == flagMask, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_EXTENSION_ROW_MISSING, String.format("%s - missing row flag %d", name, passFlag)));
            }
            if (numRows == 0) {
                Assert.fail(ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_EXTENSION_ROW_MISSING, "at least one mapping table row"));
            }
        }
    }

    @Test(description = "See OGC 18-000: Requirement 4")
    public void relations_table_definition() throws SQLException {

        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkgext_relations"), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, "gpkgext_relations"));
        try (
                final Statement statement = this.databaseConnection.createStatement();
                final ResultSet resultSet = statement.executeQuery("PRAGMA table_info('gpkgext_relations')");) {
            int passFlag = 0;
            final int flagMask = 0b0111111;

            checkPrimaryKey("gpkgext_relations", "id");

            while (resultSet.next()) {
                final String name = resultSet.getString("name");
                if ("id".equals(name)) {
                    // handled with checkPrimaryKey...
                    passFlag |= 1;
                } else if ("base_table_name".equals(name)) {
                    assertEquals(resultSet.getString("type"), "TEXT", ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_RELATIONS_COLUMN_INVALID, "base_table_name type"));
                    assertTrue(resultSet.getInt("notnull") == 1, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_RELATIONS_COLUMN_INVALID, "base_table_name notnull"));
                    passFlag |= (1 << 1);
                } else if ("base_primary_column".equals(name)) {
                    assertEquals(resultSet.getString("type"), "TEXT", ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_RELATIONS_COLUMN_INVALID, "base_primary_column type"));
                    assertTrue(resultSet.getInt("notnull") == 1, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_RELATIONS_COLUMN_INVALID, "base_primary_column notnull"));
                    final String def = resultSet.getString("dflt_value");
                    assertTrue("'id'".equals(def) || "id".equals(def), ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_RELATIONS_COLUMN_INVALID, "base_primary_column default"));
                    passFlag |= (1 << 2);
                } else if ("related_table_name".equals(name)) {
                    assertEquals(resultSet.getString("type"), "TEXT", ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_RELATIONS_COLUMN_INVALID, "related_table_name type"));
                    assertTrue(resultSet.getInt("notnull") == 1, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_RELATIONS_COLUMN_INVALID, "related_table_name notnull"));
                    passFlag |= (1 << 3);
                } else if ("related_primary_column".equals(name)) {
                    assertEquals(resultSet.getString("type"), "TEXT", ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_RELATIONS_COLUMN_INVALID, "related_primary_column type"));
                    assertTrue(resultSet.getInt("notnull") == 1, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_RELATIONS_COLUMN_INVALID, "related_primary_column notnull"));
                    final String def = resultSet.getString("dflt_value");
                    assertTrue("'id'".equals(def) || "id".equals(def), ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_RELATIONS_COLUMN_INVALID, "related_primary_column default"));
                    passFlag |= (1 << 4);
                } else if ("relation_name".equals(name)) {
                    assertEquals(resultSet.getString("type"), "TEXT", ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_RELATIONS_COLUMN_INVALID, "relation_name type"));
                    assertTrue(resultSet.getInt("notnull") == 1, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_RELATIONS_COLUMN_INVALID, "relation_name notnull"));
                    passFlag |= (1 << 5);
                } else if ("mapping_table_name".equals(name)) {
                    assertEquals(resultSet.getString("type"), "TEXT", ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_RELATIONS_COLUMN_INVALID, "mapping_table_name type"));
                    assertTrue(resultSet.getInt("notnull") == 1, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_RELATIONS_COLUMN_INVALID, "mapping_table_name notnull"));
                    passFlag |= (1 << 6);
                    // TODO: check for uniqueness
                }
            }
            assertTrue((passFlag & flagMask) == flagMask, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_RELATIONS_COLUMN_INVALID, "missing column(s)"));
        }
    }

    private List<String> getRowsForRelationColumn(final String column) throws SQLException {
        List<String> rows = new ArrayList<>();
        try (
                final Statement stmt = this.databaseConnection.createStatement();
                ResultSet resultSet = stmt.executeQuery("SELECT " + column + " FROM gpkgext_relations")) {
            while (resultSet.next()) {
                rows.add(resultSet.getString(column));
            }
        }
        return rows;
    }

    /*
    private List<String> getRowsForRelationColumn(final String column, final String relation) throws SQLException {
        List<String> rows = new ArrayList<>();
        try (
                final Statement stmt = this.databaseConnection.createStatement();
                ResultSet resultSet = stmt.executeQuery("SELECT " + column + " FROM gpkgext_relations WHERE relation_name = '" + relation + "'")) {
            while (resultSet.next()) {
                rows.add(resultSet.getString(column));
            }
        }
        return rows;
    }
     */
    private boolean isTableListedInContentsTable(final String tableName) throws SQLException {
        try (final PreparedStatement preparedStatement = this.databaseConnection.prepareStatement("SELECT COUNT(*) FROM gpkg_contents WHERE table_name = ? LIMIT 1;")) {
            preparedStatement.setString(1, tableName);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.getInt(1) > 0;
            }
        }
    }

    /*
    private boolean isTableListedInContentsTableAsAttributesType(final String tableName) throws SQLException {
        try (final PreparedStatement preparedStatement = this.databaseConnection.prepareStatement("SELECT COUNT(*) FROM gpkg_contents WHERE data_type = 'attributes' AND table_name = ? LIMIT 1;")) {
            preparedStatement.setString(1, tableName);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.getInt(1) > 0;
            }
        }
    }
     */
    /**
     * Test case {@code /conf/table-defs/ger-base}
     *
     *
     * @throws SQLException If an SQL query causes an error
     */
    @Test(description = "See OGC 18-000: Requirement 5")
    public void base_tables() throws SQLException {
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkgext_relations"), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, "gpkgext_relations"));
        List<String> baseTables = getRowsForRelationColumn("base_table_name");
        for (String baseTable : baseTables) {
            assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, baseTable), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, baseTable));
        }
    }

    /**
     * Test case {@code /conf/table-defs/ger-base-contents}
     *
     *
     * @throws SQLException If an SQL query causes an error
     */
    @Test(description = "See OGC 18-000: Requirement 5 - Contents")
    public void base_tables_contents() throws SQLException {
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkgext_relations"), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, "gpkgext_relations"));
        List<String> baseTables = getRowsForRelationColumn("base_table_name");
        for (String baseTable : baseTables) {
            assertTrue(isTableListedInContentsTable(baseTable), ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_RELATIONS_BASE_TABLE_NOT_IN_CONTENTS, baseTable));
        }
    }

    /**
     * Test case {@code /conf/table-defs/ger-related}
     *
     *
     * @throws SQLException If an SQL query causes an error
     */
    @Test(description = "See OGC 18-000: Requirement 6")
    public void related_tables() throws SQLException {
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkgext_relations"), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, "gpkgext_relations"));
        List<String> relatedTables = getRowsForRelationColumn("related_table_name");
        for (String relatedTable : relatedTables) {
            assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, relatedTable), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, relatedTable));
        }
    }

    /**
     * Test case {@code /conf/table-defs/ger-related-contents}
     *
     *
     * @throws SQLException If an SQL query causes an error
     */
    @Test(description = "See OGC 18-000: Requirement 6 - Contents")
    public void related_tables_contents() throws SQLException {
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkgext_relations"), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, "gpkgext_relations"));
        List<String> relatedTables = getRowsForRelationColumn("related_table_name");
        for (String relatedTable : relatedTables) {
            assertTrue(isTableListedInContentsTable(relatedTable), ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_RELATIONS_RELATED_TABLE_NOT_IN_CONTENTS, relatedTable));
        }
    }

    /**
     * Test case {@code /conf/table-defs/ger-udmt}
     *
     *
     * @throws SQLException If an SQL query causes an error
     */
    @Test(description = "See OGC 18-000: Requirement 7")
    public void mapping_tables() throws SQLException {
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkgext_relations"), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, "gpkgext_relations"));
        List<String> mappingTables = getRowsForRelationColumn("mapping_table_name");
        for (String mappingTable : mappingTables) {
            assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, mappingTable), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, mappingTable));
        }
    }

    /**
     * Test case {@code /conf/table-defs/ger-relname}
     *
     *
     * @throws SQLException If an SQL query causes an error
     */
    @Test(description = "See OGC 18-000: Requirement 8")
    public void relation_names() throws SQLException {
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkgext_relations"), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, "gpkgext_relations"));
        try (
                final Statement stmt = this.databaseConnection.createStatement();
                ResultSet resultSet = stmt.executeQuery("SELECT base_table_name, relation_name FROM gpkgext_relations WHERE (relation_name NOT IN ('features', 'simple_attributes', 'media') AND relation_name NOT LIKE 'x-_%\\__%' ESCAPE '\\')")) {
            while (resultSet.next()) {
                Assert.fail(ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_RELATION_NAME_INVALID, resultSet.getString(1), resultSet.getString(2)));
            }
        }
    }

    /**
     * Test case {@code /conf/table-defs/udmt}
     *
     * @throws SQLException If an SQL query causes an error
     */
    @Test(description = "See OGC 18-000: Requirement 9")
    public void mapping_tables_schema() throws SQLException {
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkgext_relations"), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, "gpkgext_relations"));
        List<String> mappingTables = getRowsForRelationColumn("mapping_table_name");
        for (String mappingTable : mappingTables) {
            checkMappingTableSchema(mappingTable);
        }
    }

    private void checkMappingTableSchema(final String mappingTable) throws SQLException {
        try (
                final Statement statement = this.databaseConnection.createStatement();
                final ResultSet resultSet = statement.executeQuery("PRAGMA table_info('" + mappingTable + "')");) {
            int passFlag = 0;
            final int flagMask = 0b011;

            while (resultSet.next()) {
                final String name = resultSet.getString("name");
                if ("base_id".equals(name)) {
                    assertEquals(resultSet.getString("type"), "INTEGER", ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MAPPING_COLUMN_INVALID, mappingTable, "base_id type"));
                    assertEquals(resultSet.getInt("notnull"), 1, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MAPPING_COLUMN_INVALID, mappingTable, "base_id notnull"));
                    assertEquals(resultSet.getString("dflt_value"), null, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MAPPING_COLUMN_INVALID, mappingTable, "base_id default value"));
                    assertEquals(resultSet.getInt("pk"), 0, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MAPPING_COLUMN_INVALID, mappingTable, "base_id primary key"));
                    // TODO: unique key constraints
                    passFlag |= 1;
                } else if ("related_id".equals(name)) {
                    assertEquals(resultSet.getString("type"), "INTEGER", ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MAPPING_COLUMN_INVALID, mappingTable, "related_id type"));
                    assertTrue(resultSet.getInt("notnull") == 1, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MAPPING_COLUMN_INVALID, mappingTable, "related_id notnull"));
                    assertEquals(resultSet.getString("dflt_value"), null, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MAPPING_COLUMN_INVALID, mappingTable, "related_id default value"));
                    assertEquals(resultSet.getInt("pk"), 0, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MAPPING_COLUMN_INVALID, mappingTable, "related_id primary key"));
                    // TODO: unique key constraints
                    passFlag |= (1 << 1);
                }
            }
            assertTrue((passFlag & flagMask) == flagMask, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MAPPING_COLUMN_INVALID, mappingTable, "missing column(s)"));
        }
    }

    /**
     * Test case {@code /code/table-defs/udmt-base}
     *
     * @throws SQLException If an SQL query causes an error
     */
    @Test(description = "See OGC 18-000: Requirement 10")
    public void mapping_table_base() throws SQLException {
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkgext_relations"), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, "gpkgext_relations"));
        try (
                final Statement statement = this.databaseConnection.createStatement();
                final ResultSet resultSet = statement.executeQuery("SELECT base_table_name, base_primary_column, mapping_table_name FROM gpkgext_relations");) {
            while (resultSet.next()) {
                final String base_table_name = resultSet.getString("base_table_name");
                final String base_primary_column = resultSet.getString("base_primary_column");
                final String mapping_table_name = resultSet.getString("mapping_table_name");

                check_mapping_table_base_ids(mapping_table_name, base_table_name, base_primary_column);
                check_is_pk(base_table_name, base_primary_column);
            }
        }
    }

    private void check_mapping_table_base_ids(final String mapping_table_name, final String base_table_name, final String base_primary_column) throws SQLException {
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, mapping_table_name), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, mapping_table_name));
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, base_table_name), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, base_table_name));
        List<Integer> mappingTableIds = getBaseIdsForMappingTable(mapping_table_name);
        List<Integer> baseIds = getIntegerValues(base_table_name, base_primary_column);

        for (int mappingTableId : mappingTableIds) {
            assertTrue(baseIds.contains(mappingTableId), ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MAPPING_BASE_ROW_INVALID, mapping_table_name, mappingTableId));
        }
    }

    private void check_mapping_table_related_ids(final String mapping_table_name, final String related_table_name, final String related_primary_column) throws SQLException {
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, mapping_table_name), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, mapping_table_name));
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, related_table_name), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, related_table_name));
        List<Integer> mappingTableIds = getRelatedIdsForMappingTable(mapping_table_name);
        List<Integer> relatedIds = getIntegerValues(related_table_name, related_primary_column);

        for (int mappingTableId : mappingTableIds) {
            assertTrue(relatedIds.contains(mappingTableId), ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MAPPING_RELATED_ROW_INVALID, mapping_table_name, mappingTableId));
        }
    }

    private List<Integer> getIntegerValues(final String base_table_name, final String base_primary_column) throws SQLException {
        List<Integer> rows = new ArrayList<>();
        try (
                final Statement stmt = this.databaseConnection.createStatement();
                ResultSet resultSet = stmt.executeQuery("SELECT " + base_primary_column + " FROM " + base_table_name)) {
            while (resultSet.next()) {
                rows.add(resultSet.getInt(base_primary_column));
            }
        }
        return rows;
    }

    private List<Integer> getBaseIdsForMappingTable(final String mapping_table_name) throws SQLException {
        return getIdsForMappingTable("base_id", mapping_table_name);
    }

    private List<Integer> getRelatedIdsForMappingTable(final String mapping_table_name) throws SQLException {
        return getIdsForMappingTable("related_id", mapping_table_name);
    }

    private List<Integer> getIdsForMappingTable(final String column, final String mapping_table_name) throws SQLException {
        List<Integer> rows = new ArrayList<>();
        try (
                final Statement stmt = this.databaseConnection.createStatement();
                ResultSet resultSet = stmt.executeQuery("SELECT DISTINCT " + column + " FROM " + mapping_table_name)) {
            while (resultSet.next()) {
                rows.add(resultSet.getInt(column));
            }
        }
        return rows;
    }

    private void check_is_pk(final String table_name, final String primary_column) throws SQLException {
        String pk = getPrimaryKeyColumn(table_name);
        assertEquals(pk, primary_column, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_NOT_PRIMARY_KEY, table_name, primary_column, pk));
    }

    /**
     * Test case {@code /conf/table-defs/udmt-related}
     *
     * @throws SQLException If an SQL query causes an error
     */
    @Test(description = "See OGC 18-000: Requirement 11")
    public void mapping_table_related() throws SQLException {
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkgext_relations"), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, "gpkgext_relations"));
        try (
                final Statement statement = this.databaseConnection.createStatement();
                final ResultSet resultSet = statement.executeQuery("SELECT related_table_name, related_primary_column, mapping_table_name FROM gpkgext_relations");) {
            while (resultSet.next()) {
                final String related_table_name = resultSet.getString("related_table_name");
                final String related_primary_column = resultSet.getString("related_primary_column");
                final String mapping_table_name = resultSet.getString("mapping_table_name");

                check_mapping_table_related_ids(mapping_table_name, related_table_name, related_primary_column);
                check_is_pk(related_table_name, related_primary_column);
            }
        }
    }

    // TODO: everything after this is untested junk
/*
    @Test(description = "See OGC 18-000: Requirement 7")
    public void mapping_table_def() throws SQLException {
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkgext_relations"), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, "gpkgext_relations"));
        List<String> mappingTables = getRowsForRelationColumn("mapping_table_name");
        for (String mappingTable : mappingTables) {
            checkMappingTableSchema(mappingTable);
        }
    }





    @Test(description = "See OGC 18-000: Requirement 10")
    public void is_attributes_table() throws SQLException {
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkgext_relations"), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, "gpkgext_relations"));
        List<String> relatedTables = getRowsForRelationColumn("related_table_name", "media");
        for (String relatedTable : relatedTables) {
            assertTrue(isTableListedInContentsTableAsAttributesType(relatedTable), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, relatedTable));
        }
    }

    private void checkMediaTableSchema(final String mediaTable) throws SQLException {
        try (
                final Statement statement = this.databaseConnection.createStatement();
                final ResultSet resultSet = statement.executeQuery("PRAGMA table_info('" + mediaTable + "')");) {
            int passFlag = 0;
            final int flagMask = 0b011;

            String pk = getPrimaryKeyColumn(mediaTable);
            assertNotNull(pk, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_ATTRIBUTES_NO_PRIMARY_KEY, mediaTable));

            while (resultSet.next()) {
                final String name = resultSet.getString("name");
                if ("data".equals(name)) {
                    assertEquals(resultSet.getString("type"), "BLOB", ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MEDIA_COLUMN_INVALID, mediaTable, "data type"));
                    assertTrue(resultSet.getInt("notnull") == 1, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MEDIA_COLUMN_INVALID, mediaTable, "data notnull"));
                    passFlag |= (1 << 0);
                } else if ("content_type".equals(name)) {
                    assertEquals(resultSet.getString("type"), "TEXT", ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MEDIA_COLUMN_INVALID, mediaTable, "content_type type"));
                    assertTrue(resultSet.getInt("notnull") == 1, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MEDIA_COLUMN_INVALID, mediaTable, "content_type notnull"));
                    passFlag |= (1 << 1);
                }
            }
            assertTrue((passFlag & flagMask) == flagMask, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MEDIA_COLUMN_INVALID, mediaTable, "missing column(s)"));
        }
    }

    private List<String> getRowsForRelatedMedia() throws SQLException {
        return getRowsForRelatedTable("media");
    }

    private List<String> getRowsForRelatedSimpleAttributes() throws SQLException {
        return getRowsForRelatedTable("simple_attributes");
    }

    private List<String> getRowsForRelatedTable(String relationName) throws SQLException {
        List<String> rows = new ArrayList<>();
        try (
                final Statement stmt = this.databaseConnection.createStatement();
                ResultSet resultSet = stmt.executeQuery("SELECT related_table_name FROM gpkgext_relations WHERE relation_name = '" + relationName + "'")) {
            while (resultSet.next()) {
                rows.add(resultSet.getString("related_table_name"));
            }
        }
        return rows;
    }

    @Test(description = "See OGC 18-000: Requirement 12")
    public void media_table_def() throws SQLException {
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkgext_relations"), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, "gpkgext_relations"));
        List<String> mediaTables = getRowsForRelatedMedia();
        for (String mediaTable : mediaTables) {
            checkMediaTableSchema(mediaTable);
        }
    }

    private void checkSimpleAttributesTableSchema(final String mediaTable) throws SQLException {
        try (
                final Statement statement = this.databaseConnection.createStatement();
                final ResultSet resultSet = statement.executeQuery("PRAGMA table_info('" + mediaTable + "')");) {

            String pk = getPrimaryKeyColumn(mediaTable);
            assertNotNull(pk, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_ATTRIBUTES_NO_PRIMARY_KEY, mediaTable));

            while (resultSet.next()) {
                String columnName = resultSet.getString("name");
                String columnType =  resultSet.getString("type").toUpperCase();
                assertFalse(columnType == "BLOB" || resultSet.getString("type") == "NULL", ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_SIMPLE_ATTR_COLUMN_INVALID, mediaTable, columnName));
            }
        }
    }

    @Test(description = "See OGC 18-000: Requirement 14")
    public void simple_attributes_table_def() throws SQLException {
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkgext_relations"), ErrorMessage.format(ErrorMessageKeys.MISSING_TABLE, "gpkgext_relations"));
        List<String> simpleAttributesTables = getRowsForRelatedSimpleAttributes();
        for (String simpleAttributesTable : simpleAttributesTables) {
            checkSimpleAttributesTableSchema(simpleAttributesTable);
        }
    }
     */
}
