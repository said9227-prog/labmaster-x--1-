package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.PdfViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfReaderScreen(
    bookId: String?,
    pdfViewModel: PdfViewModel,
    onBack: () -> Unit
) {
    val books by pdfViewModel.allBooks.collectAsState()
    val book = books.firstOrNull { it.id == bookId } ?: books.firstOrNull()

    if (book == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("الكتاب غير متوفر.")
        }
        return
    }

    var currentPage by remember { mutableStateOf(book.lastReadPage.coerceAtLeast(1)) }
    var pageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var totalPages by remember { mutableStateOf(book.pageCount.coerceAtLeast(1)) }

    LaunchedEffect(book, currentPage) {
        try {
            val file = File(book.localFilePath)
            if (file.exists() && file.length() > 0) {
                val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = PdfRenderer(pfd)
                totalPages = renderer.pageCount
                val pageIndex = (currentPage - 1).coerceIn(0, totalPages - 1)
                val page = renderer.openPage(pageIndex)

                val targetWidth = (page.width * 1.5f).toInt().coerceAtMost(1200)
                val targetHeight = (page.height * 1.5f).toInt().coerceAtMost(1800)
                val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                pageBitmap = bitmap
                page.close()
                renderer.close()
                pfd.close()

                pdfViewModel.updateReadingProgress(book.id, currentPage)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(book.title, maxLines = 1, style = MaterialTheme.typography.titleSmall) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { pdfViewModel.addBookmark(book.id, currentPage, "إشارة مرجعية صفحة $currentPage") }) {
                        Icon(Icons.Default.BookmarkBorder, contentDescription = "Bookmark")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (currentPage > 1) currentPage-- },
                        enabled = currentPage > 1
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Prev Page")
                    }

                    Text("صفحة $currentPage من $totalPages", fontWeight = FontWeight.Bold)

                    IconButton(
                        onClick = { if (currentPage < totalPages) currentPage++ },
                        enabled = currentPage < totalPages
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next Page")
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF1E1E1E)),
            contentAlignment = Alignment.Center
        ) {
            pageBitmap?.let { bmp ->
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "PDF Page $currentPage",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            } ?: Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(10.dp))
                Text("جاري عرض الصفحة $currentPage محلياً...", color = Color.White)
            }
        }
    }
}
