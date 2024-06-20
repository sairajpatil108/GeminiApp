package com.example.geminiapp

import android.net.Uri
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation



@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeScreen(
	bakingViewModel: BakingViewModel = viewModel()

) {
	val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
	val placeholderPrompt = stringResource(R.string.prompt_placeholder)
	val placeholderResult = stringResource(R.string.results_placeholder)
	var prompt by rememberSaveable { mutableStateOf(placeholderPrompt) }
	var result by rememberSaveable { mutableStateOf(placeholderResult) }
	val uiState by bakingViewModel.uiState.collectAsState()
	val context = LocalContext.current
	val focusManager = LocalFocusManager.current


	val imagePickerLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.GetContent()
	) { uri: Uri? ->
		selectedImageUri.value = uri
	}


	Scaffold(
		modifier = Modifier.padding(0.dp),
		topBar = {
			TopAppBar(
				title = { Text("Gemini") },
			)
		},
		content = { padding ->
			Column(modifier = Modifier.padding(padding)) {
				// Message history area (to be implemented)
				Box(modifier = Modifier.weight(1f)) {
					Box(
						modifier = Modifier
							.padding(8.dp)
							.requiredSize(200.dp)
							.border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))

					) {
						selectedImageUri.value?.let { uri ->
							Image(
								painter = rememberAsyncImagePainter(
									model = ImageRequest.Builder(context)
										.data(uri)
										.transformations(CircleCropTransformation())
										.build()
								),
								contentDescription = null,
								modifier = Modifier.fillMaxSize()
							)
						} ?: run {
							Text(
								text = "Select an image",
								modifier = Modifier.align(Alignment.Center),
								textAlign = TextAlign.Center
							)
						}
					}
				}
				Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
					if (uiState is UiState.Loading) {
						CircularProgressIndicator(modifier = Modifier
							)
					} else {
						var textColor = MaterialTheme.colorScheme.onSurface
						if (uiState is UiState.Error) {
							textColor = MaterialTheme.colorScheme.error
							result = (uiState as UiState.Error).errorMessage
						} else if (uiState is UiState.Success) {
							textColor = MaterialTheme.colorScheme.onSurface
							result = (uiState as UiState.Success).outputText
						}
						val scrollState = rememberScrollState()
						Text(
							text = result,
							textAlign = TextAlign.Start,
							color = textColor,
							modifier = Modifier
								.padding(16.dp)
								.fillMaxSize()
								.verticalScroll(scrollState)
						)
					}
				}
				Surface(
					modifier = Modifier.fillMaxWidth()
				) {
					Row(modifier = Modifier.fillMaxWidth(),
						verticalAlignment = Alignment.CenterVertically) {
						OutlinedTextField(
							value = prompt,
							label = { Text(stringResource(R.string.label_prompt)) },
							onValueChange = { prompt = it },
							modifier = Modifier
								.weight(1f)
								.padding(10.dp),
							shape = RoundedCornerShape(20.dp),// Expand the text field
							placeholder = { Text("Type your message") },
						)

						IconButton(onClick = {
							imagePickerLauncher.launch("image/*")
						}) {
							Icon(Icons.Rounded.AddAPhoto, contentDescription = "Send")
						}

						IconButton(onClick = {
							selectedImageUri.value?.let { uri ->
								val inputStream = context.contentResolver.openInputStream(uri)
								val bitmap = BitmapFactory.decodeStream(inputStream)
								bakingViewModel.sendPrompt(bitmap, prompt)
							} ;focusManager.clearFocus()
						},enabled = prompt.isNotEmpty() || selectedImageUri.value != null
						) {
							Icon(Icons.Rounded.Send, contentDescription = "SelectImage")
						}
					}
				}
			}
		}
	)
}
