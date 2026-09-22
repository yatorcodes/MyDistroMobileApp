package com.emmanuelyator.mydistro.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.emmanuelyator.mydistro.core.designsystem.component.BottomNavItem
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroBottomBar
import com.emmanuelyator.mydistro.core.model.UserRole
import com.emmanuelyator.mydistro.feature.driver.deliveryconfirmation.DeliveryConfirmationRoute
import com.emmanuelyator.mydistro.feature.driver.login.DriverLoginRoute
import com.emmanuelyator.mydistro.feature.driver.tripdetails.TripDetailsRoute
import com.emmanuelyator.mydistro.feature.driver.trips.DriverTripsRoute
import com.emmanuelyator.mydistro.feature.placeholder.ComingSoonScreen
import com.emmanuelyator.mydistro.feature.rolepicker.RoleSelectionScreen
import com.emmanuelyator.mydistro.feature.splash.SplashScreen
import androidx.compose.runtime.getValue

private const val TRANSITION_MILLIS = 260

/**
 * Single navigation host for the app.
 *
 * The driver's four tabs live inside the same NavHost rather than a nested one,
 * with the bottom bar shown only for routes in [DriverShellRoutes]. That keeps
 * detail screens full-screen without a second navigation controller to reason
 * about.
 */
@Composable
fun MyDistroNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showDriverShell = currentRoute in DriverShellRoutes

    val driverTabs = remember { driverBottomNavItems() }

    Column(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            // weight, not fillMaxSize: the bottom bar below has to keep its
            // space rather than being pushed off screen.
            modifier = Modifier.weight(1f),
            enterTransition = {
                slideInHorizontally(tween(TRANSITION_MILLIS)) { it / 6 } +
                    fadeIn(tween(TRANSITION_MILLIS))
            },
            exitTransition = { fadeOut(tween(TRANSITION_MILLIS)) },
            popEnterTransition = { fadeIn(tween(TRANSITION_MILLIS)) },
            popExitTransition = {
                slideOutHorizontally(tween(TRANSITION_MILLIS)) { it / 6 } +
                    fadeOut(tween(TRANSITION_MILLIS))
            }
        ) {
            onboardingGraph(navController)
            driverGraph(navController)
            customerGraph(navController)
        }

        if (showDriverShell) {
            MyDistroBottomBar(
                items = driverTabs,
                currentRoute = currentRoute,
                onItemClick = { item ->
                    navController.navigate(item.route) {
                        // Single instance per tab, and returning to Trips pops
                        // the others rather than stacking them.
                        popUpTo(Routes.DRIVER_TRIPS) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

private fun NavGraphBuilder.onboardingGraph(navController: NavHostController) {
    composable(Routes.SPLASH) {
        SplashScreen(
            onFinished = {
                navController.navigate(Routes.ROLE_SELECTION) {
                    // The splash must not be reachable by back.
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            }
        )
    }

    composable(Routes.ROLE_SELECTION) {
        RoleSelectionScreen(
            onRoleSelected = { role ->
                when (role) {
                    UserRole.DRIVER -> navController.navigate(Routes.DRIVER_LOGIN)
                    // Customer sign-in is a later phase; the placeholder is
                    // honest about that rather than dead-ending.
                    UserRole.CUSTOMER -> navController.navigate(Routes.CUSTOMER_COMING_SOON)
                    UserRole.DISTRIBUTOR,
                    UserRole.FACTORY -> navController.navigate(Routes.CUSTOMER_COMING_SOON)
                }
            }
        )
    }
}

private fun NavGraphBuilder.driverGraph(navController: NavHostController) {
    composable(Routes.DRIVER_LOGIN) {
        DriverLoginRoute(
            onLoginSuccess = {
                navController.navigate(Routes.DRIVER_TRIPS) {
                    // Clear the whole onboarding stack: a signed-in driver
                    // pressing back should exit, not return to login.
                    popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                }
            },
            onForgotPassword = { navController.navigate(Routes.DRIVER_PASSWORD_SETUP) }
        )
    }

    composable(Routes.DRIVER_PASSWORD_SETUP) {
        ComingSoonScreen(
            title = "Password Setup",
            description = "Driver accounts are provisioned by your distributor. " +
                "First-time password setup and reset will be handled here once the " +
                "backend issues reset tokens.",
            onBack = navController::popBackStackSafely
        )
    }

    composable(Routes.DRIVER_TRIPS) {
        DriverTripsRoute(
            onTripClick = { tripId ->
                navController.navigate(Routes.driverTripDetails(tripId))
            }
        )
    }

    composable(Routes.DRIVER_MAP) {
        ComingSoonScreen(
            title = "Map",
            description = "Live trip tracking will appear here. It needs a maps SDK " +
                "and the driver location service, which are a later phase.",
            icon = Icons.Outlined.Map
        )
    }

    composable(Routes.DRIVER_HISTORY) {
        ComingSoonScreen(
            title = "History",
            description = "Completed trips are available now under the " +
                "\"Trip History\" tab on your trips screen.",
            icon = Icons.Outlined.History
        )
    }

    composable(Routes.DRIVER_PROFILE) {
        ComingSoonScreen(
            title = "Profile",
            description = "Driver profile, vehicle details and sign-out will live here.",
            icon = Icons.Outlined.Person
        )
    }

    composable(
        route = Routes.DRIVER_TRIP_DETAILS,
        arguments = listOf(navArgument(Routes.Args.TRIP_ID) { type = NavType.StringType })
    ) {
        TripDetailsRoute(
            onBack = navController::popBackStackSafely,
            onConfirmDelivery = { tripId, stopId ->
                navController.navigate(Routes.driverDeliveryConfirmation(tripId, stopId))
            },
            onViewMap = { navController.navigate(Routes.DRIVER_MAP) }
        )
    }

    composable(
        route = Routes.DRIVER_DELIVERY_CONFIRMATION,
        arguments = listOf(
            navArgument(Routes.Args.TRIP_ID) { type = NavType.StringType },
            navArgument(Routes.Args.STOP_ID) { type = NavType.StringType }
        )
    ) {
        DeliveryConfirmationRoute(
            onBack = navController::popBackStackSafely,
            // Returns to trip details, which re-reads the trip and shows the
            // updated stop status and progress.
            onDone = navController::popBackStackSafely
        )
    }
}

private fun NavGraphBuilder.customerGraph(navController: NavHostController) {
    composable(Routes.CUSTOMER_COMING_SOON) {
        ComingSoonScreen(
            title = "Customer App",
            description = "Product browsing, ordering, M-Pesa payment and delivery " +
                "tracking are the next vertical slice. The driver flow is built first.",
            icon = Icons.Outlined.Storefront,
            onBack = navController::popBackStackSafely
        )
    }
}

/**
 * Pops only when there is something to pop, so a double tap on back cannot
 * empty the back stack and leave a blank screen.
 */
private fun NavHostController.popBackStackSafely() {
    if (previousBackStackEntry != null) popBackStack()
}

private fun driverBottomNavItems(): List<BottomNavItem> = listOf(
    BottomNavItem(
        route = Routes.DRIVER_TRIPS,
        label = "Trips",
        selectedIcon = Icons.Filled.LocalShipping,
        unselectedIcon = Icons.Outlined.LocalShipping
    ),
    BottomNavItem(
        route = Routes.DRIVER_MAP,
        label = "Map",
        selectedIcon = Icons.Filled.Map,
        unselectedIcon = Icons.Outlined.Map
    ),
    BottomNavItem(
        route = Routes.DRIVER_HISTORY,
        label = "History",
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History
    ),
    BottomNavItem(
        route = Routes.DRIVER_PROFILE,
        label = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)
