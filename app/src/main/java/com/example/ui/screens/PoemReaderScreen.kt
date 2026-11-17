package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Poem
import com.example.model.ReaderFontStyle
import com.example.model.ReaderPreferences
import com.example.model.ReaderSurfaceMode
import com.example.model.WordFrequency
import com.example.ui.components.GenrePill
import com.example.ui.theme.NotoSansBengaliFamily
import com.example.ui.theme.NotoSerifBengaliFamily
import com.example.ui.theme.ReaderNightBackground
import com.example.ui.theme.ReaderNightInk
import com.example.ui.theme.ReaderSepiaBackground
import com.example.ui.theme.ReaderSepiaInk
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PoemReaderScreen(
    poem: Poem?,
    topWords: List<WordFrequency>,
    isBookmarked: Boolean,
    readerPreferences: ReaderPreferences,
    onBack: () -> Unit,
    onRecordRead: (Int) -> Unit,
    onToggleBookmark: (Int) -> Unit,
    onPoetClick: (String) -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onSurfaceModeChange: (ReaderSurfaceMode) -> Unit,
    onFontStyleChange: (ReaderFontStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showTypographySheet by remember { mutableStateOf(false) }

    LaunchedEffect(poem?.id) {
        if (poem != null) {
            onRecordRead(poem.id)
        }
    }

    val targetBg = when (readerPreferences.surfaceMode) {
        ReaderSurfaceMode.PAPER -> MaterialTheme.colorScheme.background
        ReaderSurfaceMode.SEPIA -> ReaderSepiaBackground
        ReaderSurfaceMode.NIGHT -> ReaderNightBackground
    }
    val targetInk = when (readerPreferences.surfaceMode) {
        ReaderSurfaceMode.PAPER -> MaterialTheme.colorScheme.onBackground
        ReaderSurfaceMode.SEPIA -> ReaderSepiaInk
        ReaderSurfaceMode.NIGHT -> ReaderNightInk
    }
    val readerBg by animateColorAsState(targetValue = targetBg, label = "reader_bg")
    val readerInk by animateColorAsState(targetValue = targetInk, label = "reader_ink")
    val mutedInk = readerInk.copy(alpha = 0.72f)
    val cardBg = when (readerPreferences.surfaceMode) {
        ReaderSurfaceMode.PAPER -> MaterialTheme.colorScheme.surfaceContainerLow
        ReaderSurfaceMode.SEPIA -> Color(0xFFECE0C8)
        ReaderSurfaceMode.NIGHT -> Color(0xFF211C19)
    }

    val verseFontFamily = if (readerPreferences.fontStyle == ReaderFontStyle.SERIF) {
        NotoSerifBengaliFamily
    } else {
        NotoSansBengaliFamily
    }
    val copiedMsg = stringResource(R.string.reader_copied_toast)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = readerBg,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("reader_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.reader_back),
                            tint = readerInk
                        )
                    }
                },
                actions = {
                    if (poem != null) {
                        IconButton(
                            onClick = { showTypographySheet = true },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("reader_typography_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.TextFields,
                                contentDescription = stringResource(R.string.reader_typography),
                                tint = readerInk
                            )
                        }
                        IconButton(
                            onClick = { onToggleBookmark(poem.id) },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("reader_bookmark_button")
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = stringResource(
                                    if (isBookmarked) R.string.reader_bookmark_remove else R.string.reader_bookmark_add
                                ),
                                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else readerInk
                            )
                        }
                        IconButton(
                            onClick = {
                                clipboardManager.setText(
                                    AnnotatedString("${poem.title}\n— ${poem.poet}\n\n${poem.text}")
                                )
                                scope.launch { snackbarHostState.showSnackbar(copiedMsg) }
                            },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("reader_copy_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ContentCopy,
                                contentDescription = stringResource(R.string.reader_copy),
                                tint = readerInk
                            )
                        }
                        IconButton(
                            onClick = {
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "${poem.title} — ${poem.poet}")
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "${poem.title}\n— ${poem.poet} (${poem.genre})\n\n${poem.text}"
                                    )
                                }
                                context.startActivity(Intent.createChooser(sendIntent, null))
                            },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("reader_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Share,
                                contentDescription = stringResource(R.string.reader_share),
                                tint = readerInk
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = readerBg)
            )
        }
    ) { innerPadding ->
        if (poem == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.reader_not_found),
                    style = MaterialTheme.typography.titleMedium,
                    color = readerInk
                )
            }
            return@Scaffold
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 680.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 12.dp)
            ) {
                GenrePill(genre = poem.genre)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = poem.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = verseFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = readerInk
                )
                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(50),
                    color = cardBg,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { onPoetClick(poem.poet) }
                        .testTag("reader_poet_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.reader_view_poet, poem.poet),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = NotoSansBengaliFamily
                            ),
                            color = readerInk
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = readerInk.copy(alpha = 0.14f))
                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = poem.text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = verseFontFamily,
                        fontSize = readerPreferences.fontSizeSp.sp,
                        lineHeight = (readerPreferences.fontSizeSp * 1.7f).sp
                    ),
                    color = readerInk,
                    modifier = Modifier.testTag("reader_poem_body")
                )

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, readerInk.copy(alpha = 0.12f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.reader_stylometry_card),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = readerInk
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ReaderStatCell(
                                label = stringResource(R.string.reader_lines),
                                value = poem.lineCount.toString(),
                                ink = readerInk,
                                mutedInk = mutedInk,
                                modifier = Modifier.weight(1f)
                            )
                            ReaderStatCell(
                                label = stringResource(R.string.reader_words),
                                value = poem.wordCount.toString(),
                                ink = readerInk,
                                mutedInk = mutedInk,
                                modifier = Modifier.weight(1f)
                            )
                            ReaderStatCell(
                                label = stringResource(R.string.reader_unique),
                                value = poem.uniqueWordCount.toString(),
                                ink = readerInk,
                                mutedInk = mutedInk,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ReaderStatCell(
                                label = stringResource(R.string.reader_ttr),
                                value = "${(poem.lexicalDiversity * 100).roundToInt()}%",
                                ink = readerInk,
                                mutedInk = mutedInk,
                                modifier = Modifier.weight(1f)
                            )
                            ReaderStatCell(
                                label = stringResource(R.string.reader_wpl),
                                value = String.format(Locale.US, "%.1f", poem.avgWordsPerLine),
                                ink = readerInk,
                                mutedInk = mutedInk,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (topWords.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = stringResource(R.string.reader_top_words),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = mutedInk
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                topWords.forEach { wf ->
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = readerBg.copy(alpha = 0.7f),
                                        border = BorderStroke(1.dp, readerInk.copy(alpha = 0.14f))
                                    ) {
                                        Text(
                                            text = "${wf.word} · ${wf.count}",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontFamily = NotoSansBengaliFamily
                                            ),
                                            color = readerInk,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }

                        if (poem.sourceUrl.isNotBlank() || poem.scrapedAt.isNotBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = readerInk.copy(alpha = 0.12f))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.reader_provenance),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = mutedInk
                            )
                            if (poem.scrapedAt.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.reader_archived_on, poem.scrapedAt.take(10)),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = mutedInk
                                )
                            }
                            if (poem.sourceUrl.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { uriHandler.openUri(poem.sourceUrl) }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.reader_source_link),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    if (showTypographySheet) {
        ModalBottomSheet(
            onDismissRequest = { showTypographySheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.reader_typography),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.reader_font_size),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "${readerPreferences.fontSizeSp.roundToInt()} sp",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Slider(
                    value = readerPreferences.fontSizeSp,
                    onValueChange = onFontSizeChange,
                    valueRange = 14f..26f,
                    steps = 5,
                    modifier = Modifier.testTag("reader_font_size_slider")
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.reader_surface),
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    ReaderSurfaceMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = readerPreferences.surfaceMode == mode,
                            onClick = { onSurfaceModeChange(mode) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = ReaderSurfaceMode.entries.size
                            ),
                            modifier = Modifier.testTag("surface_mode_${mode.name}")
                        ) {
                            Text(
                                text = when (mode) {
                                    ReaderSurfaceMode.PAPER -> stringResource(R.string.reader_surface_paper)
                                    ReaderSurfaceMode.SEPIA -> stringResource(R.string.reader_surface_sepia)
                                    ReaderSurfaceMode.NIGHT -> stringResource(R.string.reader_surface_night)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.reader_font_family),
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    ReaderFontStyle.entries.forEachIndexed { index, style ->
                        SegmentedButton(
                            selected = readerPreferences.fontStyle == style,
                            onClick = { onFontStyleChange(style) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = ReaderFontStyle.entries.size
                            ),
                            modifier = Modifier.testTag("font_style_${style.name}")
                        ) {
                            Text(
                                text = when (style) {
                                    ReaderFontStyle.SERIF -> stringResource(R.string.reader_font_serif)
                                    ReaderFontStyle.SANS -> stringResource(R.string.reader_font_sans)
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun ReaderStatCell(
    label: String,
    value: String,
    ink: Color,
    mutedInk: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = ink.copy(alpha = 0.05f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = mutedInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
