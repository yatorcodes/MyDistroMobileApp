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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.emmanuelyator.mydistro.core.designsystem.component.BottomNavItem
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroBottomBar
import com.emmanuelyator.mydistro.core.model.UserRole
import com.emmanuelyator.mydistro.feature.customer.home.CustomerHomeRoute
import com.emmanuelyator.mydistro.feature.customer.login.CustomerLoginRoute
import com.emmanuelyator.mydistro.feature.driver.deliveryconfirmation.DeliveryConfirmationRoute
import com.emmanuelyator.mydistro.feature.driver.login.DriverLoginRoute
import com.emmanuelyator.mydistro.feature.driver.tripdetails.TripDetailsRoute
import com.emmanuelyator.mydistro.feature.driver.trips.DriverTripsRoute
import com.emmanuelyator.mydistro.feature.placeholder.ComingSoonScreen
import com.emmanuelyator.mydistro.feature.rolepicker.RoleSelectionScreen
import com.emmanuelyator.mydistro.feature.splash.SplashScreen

private const val TRANSITION_MILLIS = 260

/**
 * Single navigation host for the app.
 *
 * The driver's and customer's tab shells live inside the same NavHost rather
 * than nested ones, with the bottom bar shown only for routes in
 * [DriverShellRoutes] / [CustomerShellRoutes]. That keeps detail screens
 * full-screen without a second navigation controller to reason about.
 */
@Composable
fun MyDistroNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showDriverShell = currentRoute in DriverShellRoutes
    val showCustomerShell = currentRoute in CustomerShellRoutes

    val driverTabs = remember { driverBottomNavItems() }
    val customerTabs = remember { customerBottomNavItems() }

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

        when {
            showDriverShell -> MyDistroBottomBar(
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
            showCustomerShell -> MyDistroBottomBar(
                items = customerTabs,
                currentRoute = currentRoute,
                onItemClick = { item ->
                    navController.navigate(item.route) {
                        popUpTo(Routes.CUSTOMER_HOME) { saveState = true }
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
        val viewModel: com.emmanuelyator.mydistro.feature.splash.SplashViewModel = androidx.hilt.navigation.compose.hiltViewModel()
        SplashScreen(
            onFinished = {
                val nextRoute = viewModel.getNextRoute(
                    roleSelectionRoute = Routes.ROLE_SELECTION,
                    driverTripsRoute = Routes.DRIVER_TRIPS,
                    customerHomeRoute = Routes.CUSTOMER_HOME
                )
                navController.navigate(nextRoute) {
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
                    UserRole.CUSTOMER -> navController.navigate(Routes.CUSTOMER_LOGIN)
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

    composable(
        route = Routes.DRIVER_MAP,
        arguments = listOf(navArgument(Routes.Args.TRIP_ID) {
            type = NavType.StringType
            nullable = true
        })
    ) {
        com.emmanuelyator.mydistro.feature.driver.map.DriverMapRoute()
    }



    composable(Routes.DRIVER_PROFILE) {
        val viewModel: com.emmanuelyator.mydistro.feature.driver.profile.DriverProfileViewModel = androidx.hilt.navigation.compose.hiltViewModel()
        com.emmanuelyator.mydistro.feature.driver.profile.DriverProfileRoute(
            onSignOut = {
                viewModel.logout {
                    navController.navigate(Routes.ROLE_SELECTION) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            },
            onEditProfile = { navController.navigate(Routes.DRIVER_EDIT_PROFILE) },
            onNotifications = { navController.navigate(Routes.DRIVER_NOTIFICATIONS) },
            onPrivacySecurity = { navController.navigate(Routes.DRIVER_PRIVACY_SECURITY) },
            onHelpSupport = { navController.navigate(Routes.DRIVER_HELP_SUPPORT) }
        )
    }

    composable(Routes.DRIVER_EDIT_PROFILE) {
        com.emmanuelyator.mydistro.feature.driver.profile.DriverEditProfileRoute(
            onBack = navController::popBackStackSafely
        )
    }

    composable(Routes.DRIVER_NOTIFICATIONS) {
        com.emmanuelyator.mydistro.feature.driver.profile.DriverNotificationsRoute(
            onNotificationClick = { notificationId ->
                navController.navigate(Routes.driverNotificationDetails(notificationId))
            },
            onBack = navController::popBackStackSafely
        )
    }

    composable(
        route = Routes.DRIVER_NOTIFICATION_DETAILS,
        arguments = listOf(navArgument("notificationId") { type = NavType.StringType })
    ) { backStackEntry ->
        val id = backStackEntry.arguments?.getString("notificationId") ?: ""
        
        // Mark as read immediately when opened
        androidx.compose.runtime.LaunchedEffect(id) {
            val index = com.emmanuelyator.mydistro.feature.driver.profile.MockNotificationData.notifications.indexOfFirst { it.id == id }
            if (index != -1 && !com.emmanuelyator.mydistro.feature.driver.profile.MockNotificationData.notifications[index].isRead) {
                com.emmanuelyator.mydistro.feature.driver.profile.MockNotificationData.notifications[index] = 
                    com.emmanuelyator.mydistro.feature.driver.profile.MockNotificationData.notifications[index].copy(isRead = true)
            }
        }
        
        com.emmanuelyator.mydistro.feature.driver.profile.DriverNotificationDetailsRoute(
            notificationId = id,
            onBack = navController::popBackStackSafely
        )
    }

    composable(Routes.DRIVER_PRIVACY_SECURITY) {
        com.emmanuelyator.mydistro.feature.driver.profile.DriverPrivacySecurityRoute(
            onBack = navController::popBackStackSafely
        )
    }

    composable(Routes.DRIVER_HELP_SUPPORT) {
        com.emmanuelyator.mydistro.feature.driver.profile.DriverHelpSupportRoute(
            onBack = navController::popBackStackSafely
        )
    }

    composable(
        route = Routes.DRIVER_TRIP_DETAILS,
        arguments = listOf(navArgument(Routes.Args.TRIP_ID) { type = NavType.StringType })
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString(Routes.Args.TRIP_ID) ?: ""
        TripDetailsRoute(
            onBack = navController::popBackStackSafely,
            onConfirmDelivery = { tId, stopId ->
                navController.navigate(Routes.driverDeliveryConfirmation(tId, stopId))
            },
            onViewMap = { navController.navigate(Routes.driverMap(tripId)) }
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
    composable(Routes.CUSTOMER_LOGIN) {
        CustomerLoginRoute(
            onLoginSuccess = {
                navController.navigate(Routes.CUSTOMER_HOME) {
                    popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                }
            },
            onForgotPassword = { navController.navigate(Routes.CUSTOMER_SIGNUP) },
            onCreateAccount = { navController.navigate(Routes.CUSTOMER_SIGNUP) },
            onSignUp = { navController.navigate(Routes.CUSTOMER_SIGNUP) }
        )
    }

    composable(Routes.CUSTOMER_SIGNUP) {
        ComingSoonScreen(
            title = "Create account",
            description = "Customer self-registration will live here. " +
                "For now, use the prototype login credentials shown on the login screen.",
            icon = Icons.Outlined.Storefront,
            onBack = navController::popBackStackSafely
        )
    }

    composable(Routes.CUSTOMER_HOME) {
        CustomerHomeRoute()
    }

    composable(Routes.CUSTOMER_ORDERS) {
        ComingSoonScreen(
            title = "Orders",
            description = "Order history and live tracking will live here once " +
                "checkout and the orders API are wired up.",
            icon = Icons.Outlined.ReceiptLong
        )
    }

    composable(Routes.CUSTOMER_CART) {
        ComingSoonScreen(
            title = "Cart",
            description = "Your basket and M-Pesa checkout will live here. " +
                "Tap Add on any product to start filling it.",
            icon = Icons.Outlined.ShoppingCart
        )
    }

    composable(Routes.CUSTOMER_PROFILE) {
        ComingSoonScreen(
            title = "Profile",
            description = "Shop details, delivery address and sign-out will live here.",
            icon = Icons.Outlined.Person
        )
    }

    composable(Routes.CUSTOMER_COMING_SOON) {
        ComingSoonScreen(
            title = "Web console",
            description = "Distributors and factories use the Angular web app, " +
                "not this mobile client.",
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
        route = Routes.DRIVER_MAP_BASE,
        label = "Map",
        selectedIcon = Icons.Filled.Map,
        unselectedIcon = Icons.Outlined.Map
    ),
    BottomNavItem(
        route = Routes.DRIVER_PROFILE,
        label = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)

private fun customerBottomNavItems(): List<BottomNavItem> = listOf(
    BottomNavItem(
        route = Routes.CUSTOMER_HOME,
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        route = Routes.CUSTOMER_ORDERS,
        label = "Orders",
        selectedIcon = Icons.Filled.ReceiptLong,
        unselectedIcon = Icons.Outlined.ReceiptLong
    ),
    BottomNavItem(
        route = Routes.CUSTOMER_CART,
        label = "Cart",
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart
    ),
    BottomNavItem(
        route = Routes.CUSTOMER_PROFILE,
        label = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)
