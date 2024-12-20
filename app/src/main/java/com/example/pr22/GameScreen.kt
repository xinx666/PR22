package com.example.memorygame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pr22_nikolaenko.R
import kotlinx.coroutines.delay

@Composable
fun MemoryGameApp() {
    var currentScreen by remember { mutableStateOf("MainMenu") }
    var scoreList by remember { mutableStateOf(listOf<Int>()) }

    val imageResources = listOf(
        R.drawable.bee, R.drawable.lion, R.drawable.owl, R.drawable.cat,
        R.drawable.bear, R.drawable.bird, R.drawable.cow, R.drawable.crocodile,
        R.drawable.dog, R.drawable.donkey, R.drawable.elephent, R.drawable.hedgehog,
        R.drawable.monkey, R.drawable.sheep, R.drawable.stork, R.drawable.seagull,
        R.drawable.squirrel, R.drawable.toucan
    )

    when (currentScreen) {
        "MainMenu" -> MainMenuScreen(
            onStartGame = { currentScreen = "Game" },
            onViewRecords = { currentScreen = "Records" }
        )
        "Game" -> GamePlayScreen(
            images = imageResources,
            onExitGame = { currentScreen = "MainMenu" },
            onEndGame = { finalScore ->
                scoreList = (scoreList + finalScore).sortedDescending().take(5)
                currentScreen = "Records"
            }
        )
        "Records" -> RecordsScreen(
            records = scoreList,
            onBackToMenu = { currentScreen = "MainMenu" }
        )
    }
}

@Composable
fun MainMenuScreen(onStartGame: () -> Unit, onViewRecords: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onStartGame) { Text("Start Game") }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onViewRecords) { Text("View Records") }
    }
}

@Composable
fun GamePlayScreen(
    images: List<Int>,
    onExitGame: () -> Unit,
    onEndGame: (Int) -> Unit
) {
    val cardCount = 36
    val shuffledImages = (images + images).shuffled().take(cardCount)
    var cardsState by remember { mutableStateOf(createCards(shuffledImages)) }
    var selectedCardsState by remember { mutableStateOf<List<Card>>(emptyList()) }
    var playerScore by remember { mutableStateOf(0) }
    var totalMoves by remember { mutableStateOf(0) }
    var isCheckingMatch by remember { mutableStateOf(false) }

    LaunchedEffect(selectedCardsState) {
        if (selectedCardsState.size == 2) {
            isCheckingMatch = true
            delay(500)
            totalMoves++
            if (selectedCardsState[0].image == selectedCardsState[1].image) {
                playerScore += 20
                cardsState = cardsState.map {
                    if (it.id in selectedCardsState.map { it.id }) it.copy(isVisible = false) else it
                }
            } else {
                cardsState = cardsState.map {
                    if (it.id in selectedCardsState.map { it.id }) it.copy(isFlipped = false) else it
                }
            }
            selectedCardsState = emptyList()
            isCheckingMatch = false
        }
        if (cardsState.all { !it.isVisible }) {
            onEndGame(playerScore)
        }
    }

    fun onCardClick(card: Card) {
        if (selectedCardsState.size == 2 || card.isFlipped || isCheckingMatch || !card.isVisible) return

        cardsState = cardsState.map {
            if (it.id == card.id) it.copy(isFlipped = true) else it
        }
        selectedCardsState = selectedCardsState + card
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 50.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Memory Game",
            style = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Bold)
        )
        Text("Score: $playerScore, Moves: $totalMoves")
        Spacer(Modifier.height(20.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            contentPadding = PaddingValues(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(cardsState) { card ->
                if (card.isVisible) {
                    MemoryCard(card, onCardClick = { onCardClick(it) })
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Button(onClick = { onExitGame() }) {
            Text("Exit Game")
        }
    }
}

@Composable
fun RecordsScreen(records: List<Int>, onBackToMenu: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Top Scores")
        Spacer(Modifier.height(16.dp))
        records.forEachIndexed { index, score ->
            Text("${index + 1}. $score")
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onBackToMenu) {
            Text("Back to Main Menu")
        }
    }
}

fun createCards(images: List<Int>): List<Card> {
    return images.mapIndexed { index, image -> Card(index, image) }
}

@Composable
fun MemoryCard(card: Card, onCardClick: (Card) -> Unit) {
    val imageId = if (card.isFlipped) card.image else R.drawable.cardback
    Box(
        modifier = Modifier
            .size(55.dp)
            .background(Color.White, RoundedCornerShape(4.dp))
            .clickable { onCardClick(card) }
    ) {
        ImageDisplay(id = imageId)
    }
}

@Composable
fun ImageDisplay(id: Int) {
    val painter: Painter = painterResource(id = id)
    Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}

data class Card(val id: Int, val image: Int, val isFlipped: Boolean = false, val isVisible: Boolean = true)
