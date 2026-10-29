package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.TopHeaderBar
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.TokenConnectScreen
import com.example.work.AutopilotWorker
import com.example.ui.screens.ProfilesScreen
import com.example.ui.screens.QueueScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.screens.SuggestionsScreen
import com.example.ui.theme.InstaBorder
import com.example.ui.theme.InstaRed
import com.example.ui.theme.InstaTextPrimary
import com.example.ui.theme.InstaWhite
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ReelsViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ReelsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AutopilotWorker.schedule(applicationContext)

        setContent {
            MyApplicationTheme(darkTheme = false) {
                InstaAutopilotAppContent(viewModel)
            }
        }
    }
}

@Composable
fun InstaAutopilotAppContent(viewModel: ReelsViewModel) {
    val activeProfile by viewModel.activeProfile.collectAsState()
    val allProfiles by viewModel.allProfiles.collectAsState()
    val videos by viewModel.videos.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val optimalSlots by viewModel.optimalSlots.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val connectedFolderName by viewModel.connectedFolderName.collectAsState()
    val isScanning by viewModel.isFolderScanning.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()
    val uploadStage by viewModel.uploadStage.collectAsState()
    val uploadProgress by viewModel.uploadProgress.collectAsState()
    val timeframe by viewModel.analyticsTimeframe.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val isSyncingProfile by viewModel.isSyncingProfile.collectAsState()
    val isConnecting by viewModel.isConnecting.collectAsState()
    val connectError by viewModel.connectError.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val unreviewedSuggestionsCount = comments.count { !it.isReviewed }

    // Toast notifications
    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearUserMessage()
        }
    }

    // If no account is logged in yet, show the official Instagram login screen!
    if (activeProfile == null) {
        TokenConnectScreen(
            isConnecting = isConnecting,
            error = connectError,
            onConnect = { viewModel.connectWithToken(it) }
        )
        return
    }

    // Handle back button to return to Feed tab
    BackHandler(enabled = selectedTab != 0) {
        viewModel.selectTab(0)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = InstaWhite,
        topBar = {
            TopHeaderBar(
                activeProfile = activeProfile,
                allProfiles = allProfiles,
                onSwitchProfile = { viewModel.switchProfile(it) },
                onOpenProfilesTab = { viewModel.selectTab(4) },
                onToggleAutoPilot = { viewModel.toggleAutoPilot(it) },
                onOpenSuggestionsTab = { viewModel.selectTab(3) },
                unreadSuggestionsCount = unreviewedSuggestionsCount,
                isSyncing = isSyncingProfile,
                onSyncAccount = { viewModel.syncCurrentProfile() }
            )
        },
        bottomBar = {
            Box {
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = InstaBorder,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                NavigationBar(
                    containerColor = InstaWhite,
                    tonalElevation = 0.dp,
                    modifier = Modifier.testTag("main_navigation_bar")
                ) {
                    // Tab 0: Home (Reels Feed & Queue)
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { viewModel.selectTab(0) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Feed",
                                modifier = Modifier.size(26.dp)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = InstaTextPrimary,
                            unselectedIconColor = InstaTextPrimary,
                            indicatorColor = Color.Transparent
                        )
                    )

                    // Tab 1: Professional Dashboard / Insights
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { viewModel.selectTab(1) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 1) Icons.Filled.BarChart else Icons.Outlined.BarChart,
                                contentDescription = "Insights",
                                modifier = Modifier.size(26.dp)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = InstaTextPrimary,
                            unselectedIconColor = InstaTextPrimary,
                            indicatorColor = Color.Transparent
                        )
                    )

                    // Tab 2: Content Schedule / Planner
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { viewModel.selectTab(2) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                                contentDescription = "Planner",
                                modifier = Modifier.size(26.dp)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = InstaTextPrimary,
                            unselectedIconColor = InstaTextPrimary,
                            indicatorColor = Color.Transparent
                        )
                    )

                    // Tab 3: Activity / Suggestions (Heart)
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { viewModel.selectTab(3) },
                        icon = {
                            if (unreviewedSuggestionsCount > 0) {
                                BadgedBox(badge = {
                                    Badge(containerColor = InstaRed) {
                                        Text("$unreviewedSuggestionsCount", color = Color.White, fontSize = 9.sp)
                                    }
                                }) {
                                    Icon(
                                        imageVector = if (selectedTab == 3) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                        contentDescription = "Activity",
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (selectedTab == 3) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Activity",
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = InstaTextPrimary,
                            unselectedIconColor = InstaTextPrimary,
                            indicatorColor = Color.Transparent
                        )
                    )

                    // Tab 4: Profile Avatar
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { viewModel.selectTab(4) },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = if (selectedTab == 4) 2.dp else 0.dp,
                                        color = if (selectedTab == 4) InstaTextPrimary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .padding(if (selectedTab == 4) 2.dp else 0.dp)
                            ) {
                                AsyncImage(
                                    model = activeProfile?.avatarUrl,
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = InstaTextPrimary,
                            unselectedIconColor = InstaTextPrimary,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(InstaWhite)
        ) {
            when (selectedTab) {
                0 -> QueueScreen(
                    activeProfile = activeProfile,
                    allProfiles = allProfiles,
                    videos = videos,
                    connectedFolderName = connectedFolderName,
                    isScanning = isScanning,
                    isUploading = isUploading,
                    uploadStage = uploadStage,
                    uploadProgress = uploadProgress,
                    onSelectFolder = { uri -> viewModel.onFolderSelected(uri) },
                    onScanFolder = { viewModel.scanCurrentFolder() },
                    onGenerateCaptions = { videoId, tone -> viewModel.generateCaptions(videoId, tone) },
                    onScheduleOptimal = { videoId -> viewModel.scheduleAtOptimalTime(videoId) },
                    onPublishNow = { videoId -> viewModel.publishReelNow(videoId) },
                    onDeleteVideo = { videoId -> viewModel.deleteVideo(videoId) },
                    onSwitchProfile = { viewModel.switchProfile(it) },
                    onToggleAutoPilot = {
                        viewModel.toggleAutoPilot(!(activeProfile?.isAutoPilotEnabled ?: false))
                    }
                )
                1 -> AnalyticsScreen(
                    activeProfile = activeProfile,
                    videos = videos,
                    optimalSlots = optimalSlots,
                    timeframe = timeframe,
                    onSetTimeframe = { viewModel.setAnalyticsTimeframe(it) }
                )
                2 -> ScheduleScreen(
                    activeProfile = activeProfile,
                    videos = videos,
                    optimalSlots = optimalSlots,
                    onToggleAutoPilot = { viewModel.toggleAutoPilot(it) },
                    onPublishNow = { videoId -> viewModel.publishReelNow(videoId) },
                    onRescheduleOptimal = { videoId -> viewModel.scheduleAtOptimalTime(videoId) }
                )
                3 -> SuggestionsScreen(
                    comments = comments,
                    onToggleReviewed = { id, reviewed -> viewModel.toggleCommentReviewed(id, reviewed) },
                    onConvertToNextReel = { comment -> viewModel.convertCommentToNextReelIdea(comment) }
                )
                4 -> ProfilesScreen(
                    activeProfile = activeProfile,
                    allProfiles = allProfiles,
                    onSwitchProfile = { viewModel.switchProfile(it) },
                    onConnectNewOAuth = { username, fullName, category ->
                        viewModel.connectNewProfileOAuth(username, fullName, category)
                    },
                    onToggleAutoPilot = { viewModel.toggleAutoPilot(it) },
                    onLogout = { viewModel.logout() },
                    onSyncProfile = { viewModel.syncCurrentProfile() },
                    isSyncing = isSyncingProfile,
                    onUpdateCustomStats = { followers, following, posts, fullName, category ->
                        viewModel.updateProfileCustomStats(followers, following, posts, fullName, category)
                    },
                    onRemoveProfile = { viewModel.removeProfile(it) }
                )
            }
        }
    }
}
