package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.CampusBlue
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.StatusAvailable

@Composable
fun CampusMapVisualizer(
    highlightBuildingCode: String? = null,
    highlightRoomName: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        color = Color(0xFFF1F5F9),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Campus Map",
                        tint = NavyPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Campus Building Layout",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = NavyDark
                    )
                }

                if (highlightBuildingCode != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(StatusAvailable)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Faculty Location Pinned",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StatusAvailable
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Visual Grid representation of campus blocks
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Top Row: North Campus
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampusBlockCard(
                        name = "CSE Block",
                        code = "CSE-B",
                        subtitle = "Turing Hall (Floors 1-3)",
                        isHighlighted = highlightBuildingCode == "CSE-B",
                        roomLabel = if (highlightBuildingCode == "CSE-B") highlightRoomName else null,
                        modifier = Modifier.weight(1f)
                    )
                    CampusBlockCard(
                        name = "Tech Tower",
                        code = "TT-A",
                        subtitle = "Kalam Complex (Floors G-4)",
                        isHighlighted = highlightBuildingCode == "TT-A",
                        roomLabel = if (highlightBuildingCode == "TT-A") highlightRoomName else null,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Bottom Row: Central & South Campus
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampusBlockCard(
                        name = "Main Academic",
                        code = "MAC",
                        subtitle = "Dean & Admin Complex",
                        isHighlighted = highlightBuildingCode == "MAC",
                        roomLabel = if (highlightBuildingCode == "MAC") highlightRoomName else null,
                        modifier = Modifier.weight(1f)
                    )
                    CampusBlockCard(
                        name = "Ramanujan Block",
                        code = "RSB",
                        subtitle = "Science & Math (Floors 1-3)",
                        isHighlighted = highlightBuildingCode == "RSB",
                        roomLabel = if (highlightBuildingCode == "RSB") highlightRoomName else null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CampusBlockCard(
    name: String,
    code: String,
    subtitle: String,
    isHighlighted: Boolean,
    roomLabel: String?,
    modifier: Modifier = Modifier
) {
    val bg = if (isHighlighted) Color(0xFFE0F2FE) else Color.White
    val border = if (isHighlighted) CampusBlue else Color(0xFFCBD5E1)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (isHighlighted) 2.dp else 1.dp,
                color = border,
                shape = RoundedCornerShape(8.dp)
            )
            .background(bg)
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (isHighlighted) CampusBlue else NavyDark
                )
                if (isHighlighted) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Pinned Here",
                        tint = CampusBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                lineHeight = 12.sp
            )
            if (roomLabel != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CampusBlue)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = roomLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
