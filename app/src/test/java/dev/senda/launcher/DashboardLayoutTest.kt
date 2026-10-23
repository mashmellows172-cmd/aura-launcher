package dev.senda.launcher

import dev.senda.launcher.data.*
import org.junit.Assert.*
import org.junit.Test

class DashboardLayoutTest {
    private val half=BlockStyle(BlockAnchor.HEADER,halfWidth=true)
    @Test fun halfWidthUsesSameBoundariesOnHomeAndPreview() {
        assertFalse(dashboardHalfWidth(HomeBlock.ALARM,half,279f,1f))
        assertTrue(dashboardHalfWidth(HomeBlock.ALARM,half,280f,1.5f))
        assertFalse(dashboardHalfWidth(HomeBlock.AGENDA,half,400f,1.51f))
        assertFalse(dashboardHalfWidth(HomeBlock.WIDGETS,half,800f,1f))
        assertFalse(dashboardHalfWidth(HomeBlock.ALARM,half.copy(halfWidth=false),400f,1f))
    }
    @Test fun largeTextCanGrowBeyondConfiguredMinimum() {
        assertEquals(50,informationMinHeight(BlockStyle(BlockAnchor.HEADER),1f))
        assertEquals(100,informationMinHeight(BlockStyle(BlockAnchor.HEADER,textSize=20),2f))
        assertEquals(160,informationMinHeight(BlockStyle(BlockAnchor.END,height=160),1f))
    }
}
