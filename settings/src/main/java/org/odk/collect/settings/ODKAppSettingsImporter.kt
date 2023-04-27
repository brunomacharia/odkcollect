package org.odk.collect.settings

import org.json.JSONObject
import org.odk.collect.projects.Project
import org.odk.collect.projects.ProjectsRepository
import org.odk.collect.settings.importing.ProjectDetailsCreatorImpl
import org.odk.collect.settings.importing.SettingsChangeHandler
import org.odk.collect.settings.importing.SettingsImporter
import org.odk.collect.settings.importing.SettingsImportingResult
import org.odk.collect.settings.validation.JsonSchemaSettingsValidator

class ODKAppSettingsImporter(
    projectsRepository: ProjectsRepository,
    settingsProvider: SettingsProvider,
    generalDefaults: Map<String, Any>,
    removedGeneralDefaults: Map<String, Any>,
    adminDefaults: Map<String, Any>,
    removedAdminDefaults: Map<String, Any>,
    projectColors: List<String>,
    settingsChangedHandler: SettingsChangeHandler,
    private val deviceUnsupportedSettings: JSONObject
) {

    private val settingsImporter = SettingsImporter(
        settingsProvider,
        ODKAppSettingsMigrator(settingsProvider.getMetaSettings()),
        JsonSchemaSettingsValidator { javaClass.getResourceAsStream("/client-settings.schema.json")!! },
        generalDefaults,
        removedGeneralDefaults,
        adminDefaults,
        removedAdminDefaults,
        settingsChangedHandler,
        projectsRepository,
        ProjectDetailsCreatorImpl(projectColors, generalDefaults)
    )

    fun fromJSON(json: String, project: Project.Saved): SettingsImportingResult {
        return try {
            settingsImporter.fromJSON(json, project, deviceUnsupportedSettings)
        } catch (e: Throwable) {
            SettingsImportingResult.INVALID_SETTINGS
        }
    }
}
