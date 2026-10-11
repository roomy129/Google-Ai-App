package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AvailabilityStatus
import com.example.data.model.LocationFreshness
import com.example.ui.theme.CampusBlue
import com.example.ui.theme.NavyDark
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusBusy
import com.example.ui.theme.StatusInMeeting
import com.example.ui.theme.StatusNotAvailable

@Composable
fun AvailabilityBadge(
    status: AvailabilityStatus,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    Row(
        modifier = modifier
            .testTag("availability_badge_${status.name.lowercase()}")
            .clip(RoundedCornerShape(20.dp))
            .background(status.bgColor)
            .padding(horizontal = if (compact) 8.dp else 12.dp, vertical = if (compact) 4.dp else 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 6.dp else 8.dp)
                .clip(CircleShape)
                .background(status.textColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = status.displayName,
            color = status.textColor,
            fontSize = if (compact) 11.sp else 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun LocationFreshnessBadge(
    freshness: LocationFreshness,
    modifier: Modifier = Modifier
) {
    val (color, bgColor) = when (freshness) {
        LocationFreshness.CURRENT -> StatusAvailable to Color(0xFFDCFCE7)
        LocationFreshness.POSSIBLY_OUTDATED -> StatusInMeeting to Color(0xFFFEF3C7)
        LocationFreshness.LAST_KNOWN -> CampusBlue to Color(0xFFE0F2FE)
        LocationFreshness.NOT_UPDATED -> StatusNotAvailable to Color(0xFFF1F5F9)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = freshness.label,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
