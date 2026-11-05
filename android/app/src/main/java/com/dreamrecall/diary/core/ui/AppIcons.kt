package com.dreamrecall.diary.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.dreamrecall.diary.R

object AppIcons {

    val Add: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_add)

    val ArrowBack: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_arrow_back)

    val Calendar: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_calendar)

    val Check: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_check)

    val Close: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_close)

    val Delete: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_delete)

    val Entries: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_list)

    val Lock: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_lock)

    val Refresh: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_refresh)

    val Search: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_search)

    val Star: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_star)

    val Write: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_edit)
}
