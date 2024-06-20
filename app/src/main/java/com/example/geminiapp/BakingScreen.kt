//package com.example.geminiapp
//
//import android.net.Uri
//import android.graphics.BitmapFactory
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import coil.compose.rememberImagePainter
//import coil.compose.rememberAsyncImagePainter
//import coil.request.ImageRequest
//import coil.transform.CircleCropTransformation
//
//@Composable
//fun BakingScreen(
//	bakingViewModel: BakingViewModel = viewModel()
//) {
//	val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
//	val placeholderPrompt = stringResource(R.string.prompt_placeholder)
//	val placeholderResult = stringResource(R.string.results_placeholder)
//	var prompt by rememberSaveable { mutableStateOf(placeholderPrompt) }
//	var result by rememberSaveable { mutableStateOf(placeholderResult) }
//	val uiState by bakingViewModel.uiState.collectAsState()
//	val context = LocalContext.current
//
//	// Image picker launcher
//	val imagePickerLauncher = rememberLauncherForActivityResult(
//		contract = ActivityResultContracts.GetContent()
//	) { uri: Uri? ->
//		selectedImageUri.value = uri
//	}
//
//	Column(
//		modifier = Modifier.fillMaxSize()
//	) {
//		Text(
//			text = stringResource(R.string.baking_title),
//			style = MaterialTheme.typography.titleLarge,
//			modifier = Modifier.padding(16.dp)
//		)
//
//		LazyRow(
//			modifier = Modifier.fillMaxWidth()
//		) {
//			item {
//				Box(
//					modifier = Modifier
//						.padding(8.dp)
//						.requiredSize(200.dp)
//						.border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
//						.clickable {
//							imagePickerLauncher.launch("image/*")
//						}
//				) {
//					selectedImageUri.value?.let { uri ->
//						Image(
//							painter = rememberAsyncImagePainter(
//								model = ImageRequest.Builder(context)
//									.data(uri)
//									.transformations(CircleCropTransformation())
//									.build()
//							),
//							contentDescription = null,
//							modifier = Modifier.fillMaxSize()
//						)
//					} ?: run {
//						Text(
//							text = "Select an image",
//							modifier = Modifier.align(Alignment.Center),
//							textAlign = TextAlign.Center
//						)
//					}
//				}
//			}
//		}
//
//		Row(
//			modifier = Modifier.padding(all = 16.dp)
//		) {
//			TextField(
//				value = prompt,
//				label = { Text(stringResource(R.string.label_prompt)) },
//				onValueChange = { prompt = it },
//				modifier = Modifier
//					.weight(0.8f)
//					.padding(end = 16.dp)
//					.align(Alignment.CenterVertically)
//			)
//
//			Button(
//				onClick = {
//					selectedImageUri.value?.let { uri ->
//						val inputStream = context.contentResolver.openInputStream(uri)
//						val bitmap = BitmapFactory.decodeStream(inputStream)
//						bakingViewModel.sendPrompt(bitmap, prompt)
//					}
//				},
//				enabled = prompt.isNotEmpty() && selectedImageUri.value != null,
//				modifier = Modifier.align(Alignment.CenterVertically)
//			) {
//				Text(text = stringResource(R.string.action_go))
//			}
//		}
//
//		if (uiState is UiState.Loading) {
//			CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
//		} else {
//			var textColor = MaterialTheme.colorScheme.onSurface
//			if (uiState is UiState.Error) {
//				textColor = MaterialTheme.colorScheme.error
//				result = (uiState as UiState.Error).errorMessage
//			} else if (uiState is UiState.Success) {
//				textColor = MaterialTheme.colorScheme.onSurface
//				result = (uiState as UiState.Success).outputText
//			}
//			val scrollState = rememberScrollState()
//			Text(
//				text = result,
//				textAlign = TextAlign.Start,
//				color = textColor,
//				modifier = Modifier
//					.align(Alignment.CenterHorizontally)
//					.padding(16.dp)
//					.fillMaxSize()
//					.verticalScroll(scrollState)
//			)
//		}
//	}
//}