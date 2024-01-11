package com.example.oprobalities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.oprobalities.ui.theme.OPRobalitiesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OPRobalitiesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                }
            }
        }
    }
}

@Composable
fun CombinedResultsPage(){
    Column(modifier = Modifier.fillMaxWidth()) {
        //ResultPart()
        //ResultPart()
        //ResultPart()
    }
}

@Composable
fun ResultPart(defenderList:  MutableList<Pair<String, Boolean>>,
               attackerList:  MutableList<Pair<String, Boolean>>,
               weaponList:  MutableList<Pair<String, Boolean>>,
               hits: Float){
    val headers = listOf ("Base Block:", "2+", "3+", "4+", "5+", "6+")
    val bestBlockRow = mutableListOf(2f, 3f, 4f, 5f, 6f)
    val averageWounds = bestBlockRow.map { e -> (1-((7 - e) / 6)) * hits }



    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
        .fillMaxWidth()
    ){
        headers.forEachIndexed { _, header ->
            Text(text = header,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            )
        }
    }


    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ){
        Text(text="Best Block:", textAlign = TextAlign.Center, maxLines = 2, modifier = Modifier
            .padding(8.dp)
            .weight(1f))
        bestBlockRow.forEachIndexed { _, ndRow ->
            Text(text = "$ndRow+",
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            )
        }
    }
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ){
        Text(text="Average Wounds:", textAlign = TextAlign.Center, maxLines = 2, modifier = Modifier
            .padding(8.dp)
            .weight(1f))
        averageWounds.forEach { wounds ->
            Text(text = "%.2f".format(wounds),
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            )
        }
    }
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ){
        Text(text="6++", textAlign = TextAlign.Center, maxLines = 2, modifier = Modifier
            .padding(8.dp)
            .weight(1f))
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProbabilityPage(){
    var showDefenderDialog by remember { mutableStateOf(false) }
    var showAttackerDialog by remember { mutableStateOf(false) }
    var showWeaponDialog by remember { mutableStateOf(false) }
    var qualityDialog by remember { mutableStateOf(false) }
    var attacksDialog by remember { mutableStateOf(false) }

    var defenderRules = mutableListOf (
            stringResource(R.string.shield) to false,
            "AP-()" to false,
    )
    var defenderLabel by remember { mutableStateOf("") }
        .also { it.value = defenderRules.filter { it.second }.joinToString { it.first } }
    //defenderLabel = defenderRules.filter{it.second}.joinToString{it.first}
    var weaponRules = mutableListOf (
        "AP()" to false,
        "Reliable/Sniper" to false,
        "Lock-On" to false,
        "Blast()" to false,
        "Rending" to false,
        "Poison" to false,
        "Wound on saving 1s" to false,
        "Double hits on 6s" to false,
        "Triple hits on 6s" to false,
        "AP() for Impact hits" to false
    )
    var weaponLabel by remember { mutableStateOf("") }
        .also { it.value = weaponRules.filter{ it.second}.joinToString { it.first }}

    var attackerRules = mutableListOf (
        "AP()" to false,
        "Double hits on 6s" to false,
        "Double hits on 5s" to false,
        "Extra attack on 6s" to false,
        "Extra attack on 5s" to false,
        "2 extra attacks on 6s" to false,
        "Extra wound on sep. roll" to false,
        "Impact()" to false,
        "AP() for Impact hits" to false
    )
    var attackerLabel by remember { mutableStateOf("") }
        .also { it.value = attackerRules.filter{ it.second}.joinToString{it.first}}

    var attacks by remember { mutableIntStateOf(10)}
    var quality by remember { mutableIntStateOf(4)}
    var hitChance by remember { mutableFloatStateOf(0f) }
        .also { it.floatValue = ((7-quality).toFloat()/6) }
    var averageHits by remember { mutableFloatStateOf(0F) }
        .also { it.floatValue = attacks*hitChance }
    var naturalSixes by remember { mutableStateOf(0f) }
        .also {it.value = (attacks.toFloat() / 6f).toFloat()}

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = CenterHorizontally
    ) {
        ResultPart(
            defenderList = defenderRules,
            attackerList = attackerRules,
            weaponList = weaponRules,
            hits = averageHits
        )

        OutlinedTextField(
            value = defenderLabel,
            label = {Text(text = stringResource(R.string.defender_rules_display))},
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .clickable(onClick = { showDefenderDialog = true })
                .background(Color.Transparent)
        )

        Card(modifier = Modifier.padding(top = 8.dp)){
            //Text(text="to hit: $hitChance%  for an average of $averageHits and $naturalSixes sixes",
                Text(text="to hit: %.2f%%  for an average of %.2f hits and %.2f sixes".format(hitChance,averageHits,naturalSixes),
                modifier = Modifier.padding(horizontal = 8.dp))
        }

        OutlinedTextField(
            value = weaponLabel,
            label = {Text(text = stringResource(R.string.weapon_rules_display))},
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .clickable(onClick = { showWeaponDialog = true })
                .background(Color.Transparent)
        )
        OutlinedTextField(
            value = attackerLabel,
            label = {Text(text = stringResource(R.string.attacker_rules_display))},
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .clickable(onClick = { showAttackerDialog = true })
                .background(Color.Transparent)
        )


        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
        ){
            Card(onClick = { qualityDialog = true }) {
                Text(text= stringResource(R.string.quality), fontSize= 8.sp)
                Text(text= "$quality", fontSize = 80.sp,modifier = Modifier.padding(horizontal = 20.dp))
            }
            Spacer(modifier = Modifier.padding(horizontal = 32.dp))
            Card(onClick = { attacksDialog = true }) {
                Text(text= stringResource(R.string.attacks), fontSize= 8.sp)
                Text(text= "$attacks", fontSize = 80.sp,modifier = Modifier.padding(horizontal = 20.dp))
            }
        }
        if(qualityDialog) {
            Dialog(onDismissRequest = { qualityDialog = false }) {
                Row {
                    for (q in 2..6){
                        Text(text="$q",
                            fontSize= 40.sp,
                            modifier = Modifier.clickable (onClick = {quality=q} )
                        )
                    }
                }
            }
        }
        if(attacksDialog) {
            Dialog(onDismissRequest = { attacksDialog = false }) {
                Row {
                    for (a in 2..6){
                        Text(text="$a",
                            fontSize= 40.sp,
                            modifier = Modifier.clickable (onClick = {attacks=a} )
                        )
                    }
                }
            }
        }

        if (showDefenderDialog) {
            Dialog(onDismissRequest = { showDefenderDialog = false }) {
                Card {
                    defenderRules.forEachIndexed { index, (rule, bool) ->
                        FilterChip(
                            selected = bool,
                            onClick = { (defenderRules[index]) = defenderRules[index].copy(second = !bool)},
                            label = { Text(text=rule) })
                    }
                }
            }
        }

    }
}

@Composable
fun OPRobabilities () {
    var tabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf("A", "B", "C", "ABC")

    Scaffold(
        topBar = { },
        bottomBar = {
            TabRow(
                selectedTabIndex = tabIndex,
                indicator = { },
            ) {
                tabs.forEachIndexed { index, item ->
                    Tab(text = { Text(text = item,
                                      fontSize = 16.sp,
                                      fontWeight = FontWeight.ExtraBold,
                                )
                        },
                        selected = index == tabIndex,
                        onClick = { tabIndex = index },
                        selectedContentColor = Color.Black,
                        unselectedContentColor = Color.LightGray

                    )
                }
            }
        },
        content = { contentPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                when (tabIndex) {
                    0 -> ProbabilityPage()
                    1 -> ProbabilityPage()
                    2 -> ProbabilityPage()
                    else -> CombinedResultsPage()
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun OPRobabilitiesPreview() {
    OPRobalitiesTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            OPRobabilities()
        }
    }
}