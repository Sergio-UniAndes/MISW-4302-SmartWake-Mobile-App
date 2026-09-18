package com.smartwake.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smartwake.app.ui.alarmas.AlarmasScreen
import com.smartwake.app.ui.crear.CrearAlarmaScreen
import com.smartwake.app.ui.crear.ResumenAlarmaScreen
import com.smartwake.app.ui.crear.TiempoPreparacionScreen
import com.smartwake.app.ui.crear.UbicacionesScreen
import com.smartwake.app.ui.permisos.PermisosScreen

object Routes {
    const val PERMISOS = "permisos"
    const val ALARMAS = "alarmas"
    const val CREAR = "crear"
    const val RESUMEN = "resumen"
    const val PREPARACION = "preparacion"
    const val UBICACIONES = "ubicaciones/{campo}"
    const val ORIGEN = "origen"
    const val DESTINO = "destino"

    fun ubicaciones(campo: String) = "ubicaciones/$campo"
}

/**
 * Follows the Figma prototype flow "Flujo Inicial - Crear Alarma". Back and confirm actions pop
 * back to the previous screen instead of navigating forward like the prototype links do.
 */
@Composable
fun SmartWakeNavHost(
    navController: NavHostController = rememberNavController(),
    viewModel: AlarmsViewModel = viewModel(),
) {
    val alarms by viewModel.alarms.collectAsStateWithLifecycle()
    val draft by viewModel.draft.collectAsStateWithLifecycle()

    NavHost(navController = navController, startDestination = Routes.PERMISOS) {
        composable(Routes.PERMISOS) {
            PermisosScreen(
                onEmpezar = dropUnlessResumed {
                    // Permissions onboarding shouldn't be reachable with Back once done.
                    navController.navigate(Routes.ALARMAS) {
                        popUpTo(Routes.PERMISOS) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.ALARMAS) {
            AlarmasScreen(
                alarms = alarms,
                onAddAlarm = dropUnlessResumed {
                    viewModel.startDraft()
                    navController.navigate(Routes.CREAR)
                },
                onEnabledChange = viewModel::setEnabled,
                onDelete = viewModel::delete,
            )
        }
        composable(Routes.CREAR) {
            CrearAlarmaScreen(
                draft = draft,
                onDraftChange = viewModel::updateDraft,
                onClose = dropUnlessResumed { navController.popBackStack() },
                onSave = dropUnlessResumed { navController.navigate(Routes.RESUMEN) },
                onPickOrigin = dropUnlessResumed { navController.navigate(Routes.ubicaciones(Routes.ORIGEN)) },
                onPickDestination = dropUnlessResumed { navController.navigate(Routes.ubicaciones(Routes.DESTINO)) },
                onEditPrep = dropUnlessResumed { navController.navigate(Routes.PREPARACION) },
            )
        }
        composable(
            route = Routes.UBICACIONES,
            arguments = listOf(navArgument("campo") { type = NavType.StringType }),
        ) { entry ->
            val forOrigin = entry.arguments?.getString("campo") == Routes.ORIGEN
            UbicacionesScreen(
                forOrigin = forOrigin,
                current = if (forOrigin) draft.origin else draft.destination,
                onBack = dropUnlessResumed { navController.popBackStack() },
                onConfirm = { place ->
                    if (entry.isResumed()) {
                        viewModel.updateDraft { if (forOrigin) it.copy(origin = place) else it.copy(destination = place) }
                        navController.popBackStack()
                    }
                },
            )
        }
        composable(Routes.PREPARACION) { entry ->
            TiempoPreparacionScreen(
                initialTaskIds = draft.prepTaskIds,
                initialMinutes = draft.prepMinutes,
                onBack = dropUnlessResumed { navController.popBackStack() },
                onConfirm = { taskIds, minutes ->
                    if (entry.isResumed()) {
                        viewModel.updateDraft { it.copy(prepTaskIds = taskIds, prepMinutes = minutes) }
                        navController.popBackStack()
                    }
                },
            )
        }
        composable(Routes.RESUMEN) {
            ResumenAlarmaScreen(
                draft = draft,
                onBack = dropUnlessResumed { navController.popBackStack() },
                onDepartureReminderChange = { on -> viewModel.updateDraft { it.copy(departureReminder = on) } },
                onConfirm = dropUnlessResumed {
                    viewModel.saveDraft()
                    navController.popBackStack(Routes.ALARMAS, inclusive = false)
                },
            )
        }
    }
}

/** Ignores repeated taps while a screen is already leaving (like dropUnlessResumed, but with arguments). */
private fun NavBackStackEntry.isResumed() = lifecycle.currentState == Lifecycle.State.RESUMED
