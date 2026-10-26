package dev.senda.launcher.ui

import android.animation.ValueAnimator
import android.os.SystemClock
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.RailEffects
import dev.senda.launcher.data.VisualSettings
import dev.senda.launcher.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sin

private object RailTokens {
    val column = 32.dp
    val bubbleGap = 12.dp
    const val hapticGapMs = 45L
}

/** One scrub surface; glyph transforms never move touch targets or alter the launch gesture. */
@Composable
fun AlphabetRail(
    letters: List<String>, theme: LauncherTheme = LauncherTheme.NOTHING, modifier: Modifier = Modifier,
    selectedLetter: String?, visualSettings: VisualSettings, onClick: (String) -> Unit, onScrub: (String) -> Unit,
) {
    if (letters.isEmpty()) return
    val soundFeedback = dev.senda.launcher.audio.LocalSoundFeedback.current
    val highContrast = dev.senda.launcher.data.LocalAppearance.current.highContrast
    val profile = historicalProfile(theme).takeUnless { highContrast }
    val markerShape = profile.chromeShape()
    val haptics = LocalHapticFeedback.current
    val effect = RailEffects.canonical(visualSettings.railEffect)
    val duration = visualSettings.railDurationMs.coerceIn(80,600)
    val bubbleSize = visualSettings.railBubbleSize.coerceIn(40,84).dp
    val hold = visualSettings.railBubbleHoldMs.coerceIn(0,1000)
    val intensity = visualSettings.railIntensity.coerceIn(0,100) / 100f
    var dragging by remember { mutableStateOf(false) }
    var fingerIndex by remember { mutableFloatStateOf(0f) }
    var chosen by remember { mutableStateOf<String?>(null) }
    var held by remember { mutableStateOf(false) }
    var lastHaptic by remember { mutableLongStateOf(0L) }
    var releaseToken by remember { mutableIntStateOf(0) }
    val reduced = highContrast || visualSettings.reduceMotion || effect=="NONE" || !ValueAnimator.areAnimatorsEnabled()
    val engagement = animateFloatAsState(if(dragging && !reduced)1f else 0f,
        if(reduced) snap() else if(effect=="SPRING") spring(.58f,650f*(220f/duration)*(220f/duration))
        else tween(duration, easing=FastOutSlowInEasing),label="rail-engagement")
    LaunchedEffect(releaseToken,dragging,hold) {
        if(!dragging){delay(hold.toLong());held=false}
    }
    val select:(Int,Boolean)->Unit={index,scrub->
        val letter=letters[index.coerceIn(0,letters.lastIndex)]
        if(letter!=chosen || !scrub){
            soundFeedback.play(dev.senda.launcher.data.SoundEvent.INDEX)
            chosen=letter
            if(visualSettings.railHaptics && SystemClock.uptimeMillis()-lastHaptic>=RailTokens.hapticGapMs){
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                lastHaptic=SystemClock.uptimeMillis()
            }
            if(scrub)onScrub(letter) else onClick(letter)
        }
        held=true;releaseToken++
    }
    val currentSelect by rememberUpdatedState(select)
    BoxWithConstraints(modifier.padding(vertical=4.dp)) {
        val columns=(maxWidth/RailTokens.column).toInt().coerceAtLeast(1)
        val rows=((letters.size+columns-1)/columns).coerceAtLeast(1)
        val columnWidth=maxWidth/columns
        val slot=maxHeight/rows
        val selectedIndex=letters.indexOf(if(held)chosen else selectedLetter).coerceAtLeast(0)
        val indicatorY=animateDpAsState(slot*(selectedIndex%rows),
            if(reduced || dragging && effect!="SPRING")snap()
            else if(effect=="SPRING")spring(.62f,650f*(220f/duration)*(220f/duration))
            else tween(duration,easing=FastOutSlowInEasing),label="rail-position")
        Box(Modifier.offset {
            IntOffset((columnWidth*(selectedIndex/rows)).roundToPx(),
                indicatorY.value.coerceIn(0.dp,(maxHeight-slot).coerceAtLeast(0.dp)).roundToPx())
        }.width(columnWidth).height(slot).padding(1.dp)
            .background(MaterialTheme.colorScheme.primary.copy(alpha=.18f),markerShape)
            .border(1.dp,MaterialTheme.colorScheme.primary.copy(alpha=.65f),markerShape))
        Row(Modifier.fillMaxSize().pointerInput(letters,columns,rows){
            var previous=-1
            fun move(position:Offset){
                val column=(position.x/(size.width/columns.toFloat())).toInt().coerceIn(0,columns-1)
                val row=(position.y/(size.height/rows.toFloat())).coerceIn(0f,rows-.001f)
                fingerIndex=column*rows+row-.5f
                val index=(column*rows+row.toInt()).coerceIn(0,letters.lastIndex)
                if(index!=previous){currentSelect(index,true);previous=index}
            }
            detectDragGestures(onDragStart={dragging=true;previous=-1;move(it)},
                onDrag={change,_->change.consume();move(change.position)},
                onDragEnd={dragging=false;releaseToken++},onDragCancel={dragging=false;releaseToken++})
        }){
            repeat(columns){column->
                Column(Modifier.weight(1f).fillMaxHeight()){
                    repeat(rows){row->
                        val index=column*rows+row;val letter=letters.getOrNull(index)
                        Box(Modifier.weight(1f).fillMaxWidth().then(if(letter==null)Modifier else Modifier
                            .semantics { contentDescription="Saltar a la sección $letter";role=Role.Button;selected=index==selectedIndex }
                            .clickable {select(index,false)}),contentAlignment=Alignment.Center){
                            if(letter!=null)Text(letter,Modifier.graphicsLayer {
                                // Hot state is read only in the graphics layer; no per-pixel Home recomposition.
                                translationX=0f;translationY=0f;rotationX=0f;rotationY=0f;rotationZ=0f;scaleX=1f;scaleY=1f;alpha=1f
                                val distance=index-fingerIndex
                                val influence=exp(-distance*distance*.38f)*engagement.value.coerceIn(0f,1.2f)*intensity
                                cameraDistance=10f*density
                                when(effect){
                                    "MAGNET"->{translationX=if(column==0)-26.dp.toPx()*influence else 0f;translationY=distance*9.dp.toPx()*influence;scaleX=1f+1.7f*influence;scaleY=scaleX}
                                    "WAVE"->{translationX=if(column==0)-(18f+sin(distance*.9f)*12f).dp.toPx()*influence else 0f
                                        translationY=sin(distance*1.1f)*7.dp.toPx()*influence;rotationZ=distance*11f*influence;scaleX=1f+1.3f*influence;scaleY=scaleX}
                                    "LENS"->{scaleX=1f+2f*influence;scaleY=scaleX;translationY=distance*16.dp.toPx()*influence}
                                    "CASCADE"->{translationX=if(column==0)-32.dp.toPx()*influence else 0f;translationY=distance*9.dp.toPx()*influence
                                        rotationZ=-distance*16f*influence;scaleX=1f+.8f*influence;scaleY=scaleX}
                                    "ORBIT"->{translationX=if(column==0)-28.dp.toPx()*influence else 0f;rotationY=-48f*influence
                                        rotationZ=sin(distance)*9f*influence;scaleX=1f+1.25f*influence;scaleY=scaleX}
                                    "SPRING"->{translationX=if(column==0)-22.dp.toPx()*influence else 0f;translationY=distance*9.dp.toPx()*influence;scaleX=1f+1.7f*influence;scaleY=scaleX}
                                    "FADE"->{alpha=1f-.16f*engagement.value.coerceIn(0f,1f)*(1f-exp(-distance*distance*.38f))}
                                }
                            },color=MaterialTheme.colorScheme.onSurface,fontWeight=FontWeight.SemiBold,fontSize=11.sp,lineHeight=13.sp)
                        }
                    }
                }
            }
        }
        Box(Modifier.offset {
            val y=(indicatorY.value+slot/2-bubbleSize/2).coerceIn(0.dp,(maxHeight-bubbleSize).coerceAtLeast(0.dp))
            IntOffset(-(bubbleSize+RailTokens.bubbleGap).roundToPx(),y.roundToPx())
        }){
            val enter=if(reduced)fadeIn(tween(0)) else when(effect){
                "FADE"->fadeIn(tween(duration))
                "WAVE"->fadeIn(tween(duration))+slideInHorizontally(tween(duration)){it/2}
                "CASCADE"->fadeIn(tween(duration))+slideInVertically(tween(duration)){it/3}
                else->fadeIn(tween(duration))+scaleIn(if(effect=="SPRING")spring(.6f,600f) else tween(duration),initialScale=if(effect=="LENS").4f else .7f)
            }
            AnimatedVisibility(held,enter=enter,exit=if(reduced)fadeOut(tween(0)) else fadeOut(tween(duration.coerceAtMost(220)))){
                HistoricalPanel(theme,Modifier.size(bubbleSize).clearAndSetSemantics {},selected=true){
                    Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){
                        if(effect=="FADE" && !reduced)Crossfade(letters[selectedIndex],animationSpec=tween(duration),label="rail-letter"){
                            Text(it,fontWeight=FontWeight.Bold,fontSize=(visualSettings.railBubbleSize.coerceIn(40,84)*.5f).sp,maxLines=1)
                        } else Text(letters[selectedIndex],fontWeight=FontWeight.Bold,fontSize=(visualSettings.railBubbleSize.coerceIn(40,84)*.5f).sp,maxLines=1)
                    }
                }
            }
        }
    }
}
