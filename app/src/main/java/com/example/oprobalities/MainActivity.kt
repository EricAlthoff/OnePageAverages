package com.example.oprobalities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oprobalities.ui.theme.OPRobalitiesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("Pref", Context.MODE_PRIVATE)

        setContent {
            OPRobalitiesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OPRobabilities(sharedPref)
                }
            }
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun ResultPart(inputs: InputsAndCalc, sharedPref: SharedPreferences) {
    val headers = listOf("Base", "2+", "3+", "4+", "5+", "6+") //app crashes when I remove Base, even if i adjust index
    val lefters = listOf("  Base", "  Best", "  Wounds", "  6++", "  Reg 5+", "  6++", "  Reg 4+", "  6++")
    var counter = 0
    var expandedReg by remember { mutableStateOf(sharedPref.getBoolean("always_show_Regeneration",false)) }
    var regAlpha by remember { mutableFloatStateOf(1f) }
    regAlpha = if (expandedReg)  1f else 0f
    var expandedSixPP by remember { mutableStateOf(sharedPref.getBoolean("always_show_6++",false)) }
    var sixAlpha by remember { mutableFloatStateOf(1f) }
    sixAlpha = if (expandedSixPP) 1f else 0f

    @Composable
    fun ResultText (text: String, alpha: Float) {
        Box (modifier = Modifier
            .alpha(alpha)
            .sizeIn(minWidth = 64.dp, maxWidth = 64.dp, minHeight = 40.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.tertiary,
            )
            .padding(8.dp) ,
            contentAlignment = Center
        )
        {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.alpha(alpha)
            )
        }
    }

    Row{
        Column {
            lefters.forEach { index ->
                counter += 1
                    Row {
                        when (counter) {
                            1,2,3 -> Box(
                                modifier = Modifier
                                    .sizeIn(minWidth = 84.dp, maxWidth = 124.dp, minHeight = 40.dp)
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.tertiary,
                                    )
                                    /*.background(color = Color.LightGray)*/,
                                contentAlignment = Alignment.CenterStart
                                ) {Text(text = index,textAlign = TextAlign.Center)}
                            4 -> {
                                Box(
                                    modifier = Modifier
                                        .sizeIn(
                                            minWidth = 84.dp,
                                            maxWidth = 124.dp,
                                            minHeight = 40.dp
                                        )
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.tertiary,
                                        )
                                        .clickable { expandedSixPP = !expandedSixPP },
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = index,
                                        textAlign = TextAlign.Center,
                                    )
                                    Icon(
                                        imageVector = if (expandedSixPP) Icons.Default.ArrowDropDown else Icons.Default.KeyboardArrowRight,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .align(Alignment.CenterEnd)
                                    )
                                }
                            }
                            5 -> {
                                Box(
                                    modifier = Modifier
                                        .sizeIn(
                                            minWidth = 84.dp,
                                            maxWidth = 124.dp,
                                            minHeight = 40.dp
                                        )
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.tertiary,
                                        )
                                        .clickable { expandedReg = !expandedReg },
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = index,
                                        textAlign = TextAlign.Center
                                    )
                                    Icon(
                                        imageVector = if (expandedReg) Icons.Default.ArrowDropDown else Icons.Default.KeyboardArrowRight,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .align(Alignment.CenterEnd)
                                    )
                                }
                            }
                            6,8 -> {
                                AnimatedVisibility(visible = expandedReg && expandedSixPP) {
                                    Box(
                                        modifier = Modifier
                                            .sizeIn(
                                                minWidth = 84.dp,
                                                maxWidth = 124.dp,
                                                minHeight = 40.dp
                                            )
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.tertiary,
                                            ),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(
                                            text = index,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.alpha(sixAlpha + regAlpha-1)
                                        )
                                    }
                                }
                            }
                            else -> {
                                AnimatedVisibility(visible = expandedReg) {
                                    Box(
                                        modifier = Modifier
                                            .sizeIn(
                                                minWidth = 84.dp,
                                                maxWidth = 124.dp,
                                                minHeight = 40.dp
                                            )
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.tertiary,
                                            ),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(
                                            text = index,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.alpha(regAlpha)
                                        )
                                    }
                                }
                            }
                        }
                    }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly) {
            for (index in 2..6) {
                    Column(
                        modifier = Modifier.weight(0.2f),
                        horizontalAlignment = CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        val impactBest = (inputs.combineTwoXs(9) + index).coerceIn(2..6)
                        val impactWounds = // have their own AP(X) and attacks
                            (1 - ((7 - impactBest.toFloat()) / 6)) * inputs.impactHits()
                        val currentBest =
                            (inputs.combineAPXs(0) + index - inputs.defenderRules.value[2].second).coerceIn(
                                2..6)
                        var wounds =
                            ((1 - ((7 - currentBest.toFloat()) / 6)) * inputs.averageHits()) + impactWounds + inputs.extraWounds()
                        val woundsRend =
                            ((1 - ((7 - currentBest.toFloat()) / 6)) * inputs.withoutRendingHits()) + impactWounds + ((1 - ((7 - 6).toFloat() / 6)) * inputs.rendingHits()) + inputs.extraWounds()

                        if (inputs.weaponRules.value[5].second == 1) wounds = woundsRend

                        ResultText(text = headers[index - 1], alpha = 1f)
                        ResultText(text = "$currentBest+", alpha = 1f)
                        ResultText(text = "%.2f".format(wounds), alpha = 1f)
                        ResultText(text = "%.2f".format(wounds * 5 / 6), alpha = sixAlpha)
                        ResultText(text = "%.2f".format(wounds * inputs.regenerationChance(4)), alpha = regAlpha)
                        AnimatedVisibility(visible = expandedSixPP && expandedReg) {
                            ResultText(text = "%.2f".format((wounds * inputs.regenerationChance(4)) * 5 / 6), alpha = sixAlpha+regAlpha-1)
                        }
                        AnimatedVisibility(visible = expandedReg) {
                            ResultText(text = "%.2f".format(wounds * inputs.regenerationChance(3)), alpha = regAlpha)
                        }
                        AnimatedVisibility(visible = expandedSixPP && expandedReg) {
                            ResultText(text = "%.2f".format((wounds * inputs.regenerationChance(3)) * 5 / 6), alpha = sixAlpha+regAlpha-1)
                        }
                    }
                }
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallIntDialog(
    range: IntRange,
    selectedValue: MutableState<Int>,
    onDismiss: () -> Unit
) {
    AlertDialog(onDismissRequest = { onDismiss() }) {
        Card (modifier = Modifier
            .wrapContentSize()
            .border(
                border = BorderStroke(
                    3.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.primary
                        )
                    )
                ),
                shape = MaterialTheme.shapes.medium
            )
        ){
            LazyColumn (modifier = Modifier.padding(vertical = 64.dp)){
                range.forEach { value ->
                    item {
                        FilterChip(
                            selected = selectedValue.value == value,
                            onClick = {
                                selectedValue.value = value
                                onDismiss()
                            },
                            label = { if (value == 47)
                                BadgedBox(badge = {Badge {Text (text="wtf?", fontSize = 4.sp) } } ) {
                                    Text(text="$value")
                                }
                                else Text("$value") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesStringDialog (
        textFieldName: String,
        rulesList : MutableState<List<Pair<String, Int>>>,
        inputsAndCalc: InputsAndCalc) {
    val actionSrc = remember { MutableInteractionSource() }
    val isPressed = actionSrc.collectIsPressedAsState().value
    var textFieldDialog by remember { mutableStateOf(false) }
    var extraNumberDialog by remember { mutableStateOf(false) }
    var apIndex by remember { mutableIntStateOf(0) }

    OutlinedTextField(
        value = inputsAndCalc.stringFromTrues(rulesList),
        label = { Text(textFieldName) },
        onValueChange = {},
        textStyle = TextStyle(lineHeight = 14.sp),
        readOnly = true,
        enabled = false,
        modifier = Modifier
            .clickable(onClick = { textFieldDialog = true })
            .background(Color.Transparent)
            .fillMaxWidth()
    )

    if (textFieldDialog) {
        AlertDialog(onDismissRequest = { textFieldDialog = false }) {
            Card {

                rulesList.value.forEachIndexed { index, (rule, _) ->
                    FilterChip(
                        selected = rulesList.value[index].second > 0,
                        onClick = {
                            if (rulesList.value[index].first.any { it.isDigit() }) {
                                apIndex = index
                                extraNumberDialog = true
                            } else {
                                rulesList.value =
                                    rulesList.value.mapIndexed { currentIndex, (currentRule, currentX) ->
                                        if (currentIndex == index) {
                                            rule to if (currentX == 0) 1 else 0
                                        } else {
                                            currentRule to currentX
                                        }
                                    }
                            }

                        },
                        label = { Text(text = rule, color = MaterialTheme.colorScheme.primary) },
                        interactionSource = actionSrc,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.tertiary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = MaterialTheme.colorScheme.secondary,
                            disabledBorderColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
                if (extraNumberDialog) {
                    AlertDialog(onDismissRequest = { extraNumberDialog = false })
                    {
                        Card {
                            val digitRange = inputsAndCalc.digitRangeOfX(rulesList, apIndex)
                            Row {
                                Text(text = "    Change ${rulesList.value[apIndex].first} to:")
                                if (digitRange.size > 6) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                }
                            }
                            LazyRow(
                                modifier = Modifier
                                    .background(Color.LightGray)
                                    .padding(16.dp)
                            ) {
                                digitRange.forEach { digit ->
                                    item {
                                        FilterChip(
                                            selected = rulesList.value[apIndex].first.contains(
                                                digit.toString()
                                            ),
                                            label = { Text(text = "$digit", color = MaterialTheme.colorScheme.tertiary) },
                                            interactionSource = actionSrc,
                                            onClick = {
                                                inputsAndCalc.updateRuleString(
                                                    rulesList,
                                                    apIndex,
                                                    digit
                                                )
                                                extraNumberDialog = false
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                                selectedLabelColor = MaterialTheme.colorScheme.tertiary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProbabilityPage( inputs : InputsAndCalc, sharedPref: SharedPreferences ) {
    var qualityDialog by remember { mutableStateOf(false) }
    var attacksDialog by remember { mutableStateOf(false) }
    var attackerModelsDialog by remember { mutableStateOf(false) }
    var defenderModelsDialog by remember { mutableStateOf(false) }
    var resetPage by remember { mutableStateOf(false) }

    Column {
        ResultPart(inputs = inputs, sharedPref = sharedPref)
        Row (
            verticalAlignment = Alignment.CenterVertically){
            Column (
                modifier = Modifier
                    .width(60.dp)
                    .padding(start = 4.dp)){
                Box (contentAlignment = Center) {
                    Column(
                        horizontalAlignment = CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        OutlinedTextField(
                            value = inputs.defenderModels.value.toString(),
                            onValueChange = {},
                            label = { /*Text(text=stringResource(R.string.defender))*/ },
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier
                                .clickable(onClick = { defenderModelsDialog = true })
                        )

                        OutlinedTextField(
                            value = inputs.attackerModels.value.toString(),
                            onValueChange = {},
                            label = { /*Text(stringResource(R.string.attacker))*/ },
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier
                                .clickable(onClick = { attackerModelsDialog = true })
                        )
                    }
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiary)){append("defender")}
                            append("\nmodels\n")
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiary)){append("attacker")}
                        },
                        fontSize = 12.sp,
                        style = TextStyle(lineHeight = 10.sp),
                        textAlign = TextAlign.Center
                    )
                }
                Box (contentAlignment = Center) {
                    Column {
                        OutlinedTextField(
                            value = inputs.quality.value.toString(),
                            onValueChange = {},
                            label = { /*Text(text=stringResource(R.string.defender))*/ },
                            readOnly = true,
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                            enabled = false,
                            modifier = Modifier
                                .clickable(onClick = { qualityDialog = true })
                        )
                        OutlinedTextField(
                            value = inputs.attacks.value.toString(),
                            onValueChange = {},
                            label = { /*Text(stringResource(R.string.attacker))*/ },
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier
                                .clickable(onClick = { attacksDialog = true })
                        )
                    }
                    Text(
                        buildAnnotatedString {
                            append("quality")
                            withStyle(style = SpanStyle(MaterialTheme.colorScheme.tertiary)){append("\nand\n")}
                            append("attacks")
                        },
                        fontSize = 12.sp,
                        style = TextStyle(lineHeight = 10.sp),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box (contentAlignment = Center) {
                    Text (text = stringResource(R.string.reset),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.clickable{ resetPage = true})
                }
                Spacer(modifier = Modifier.padding(10.dp))
            }
            Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                RulesStringDialog(
                    textFieldName = stringResource(R.string.defender_rules_display),
                    rulesList = inputs.defenderRules,
                    inputsAndCalc = inputs
                )
                OutlinedTextField(
                    value = "%.2f%% to hit for an average of %.2f hits, %.2f natural ⚅"
                        .format(
                            inputs.hitChance(),
                            inputs.averageHits(),
                            inputs.naturalSixes()
                        ) + inputs.extraWoundsString(),
                    onValueChange = {},
                    label = { },
                    textStyle = TextStyle(lineHeight = 14.sp),
                    readOnly = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.primary,
                    )
                )
                RulesStringDialog(
                    textFieldName = stringResource(R.string.weapon_rules_display),
                    rulesList = inputs.weaponRules,
                    inputsAndCalc = inputs
                )
                RulesStringDialog(
                    textFieldName = stringResource(R.string.attacker_rules_display),
                    rulesList = inputs.attackerRules,
                    inputsAndCalc = inputs
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
        if(attackerModelsDialog) {
            SmallIntDialog(range = 1..20, selectedValue = inputs.attackerModels) {
                attackerModelsDialog = false
            }
        }
        if(defenderModelsDialog) {
            SmallIntDialog(range = 1..20, selectedValue = inputs.defenderModels) {
                defenderModelsDialog = false
            }
        }
        if(qualityDialog) {
            SmallIntDialog(range = 2..6, selectedValue = inputs.quality) { qualityDialog = false }
        }
        if(attacksDialog) {
            SmallIntDialog(range = 1..50, selectedValue = inputs.attacks) { attacksDialog = false }
        }
        if (resetPage) {
            inputs.reset()
            resetPage = false
        }
}

@Composable
fun WelcomeInfoOptions(sharedPref: SharedPreferences){
    val scrollState = rememberScrollState()

    Column (modifier = Modifier.verticalScroll(scrollState)) {
        WelcomeInfOptionsTab(false, painterResource(id = R.drawable.circle_qmark)) {
            Text(text = stringResource(R.string.options_help), modifier = Modifier.padding(horizontal = 4.dp))
        }
        WelcomeInfOptionsTab(false, painterResource(id = R.drawable.circle_star)) {
            Text(text = stringResource(R.string.options_customize), modifier = Modifier.padding(horizontal = 4.dp))
            CustomizeColorsSharedPref()
        }
        WelcomeInfOptionsTab(false, painterResource(id = R.drawable.circle_cog)) {
            Text(text = "", modifier = Modifier.padding(horizontal = 4.dp))
            Preferences(sharedPref)
        }
        WelcomeInfOptionsTab(true, painterResource(id = R.drawable.circle_info)) {
            Text(text = stringResource(R.string.options_info), modifier = Modifier.padding(horizontal = 4.dp))
        }
    }
}

@Composable // todo color options
fun CustomizeColorsSharedPref () {
}

@Composable
fun Preferences(sharedPref: SharedPreferences){
    var alwaysShowReg by remember { mutableStateOf(sharedPref.getBoolean("always_show_Regeneration", false)) }
    var alwaysShowSix by remember { mutableStateOf(sharedPref.getBoolean("always_show_6++",false)) }

    Column {
        Text("initial visibility preferences:")
        Row {
            Checkbox(checked = alwaysShowReg, onCheckedChange = { alwaysShowReg = !alwaysShowReg } )
            Text(text = "always show Regeneration",
                modifier = Modifier.padding(top = 12.dp) )
        }
        Row {
            Checkbox(checked = alwaysShowSix, onCheckedChange = { alwaysShowSix = !alwaysShowSix } )
            Text(text = "always show 6++",
                modifier = Modifier.padding(top = 12.dp) )
        }
    }
    sharedPref.edit().apply {
        putBoolean("always_show_Regeneration", alwaysShowReg)
        putBoolean("always_show_6++", alwaysShowSix)
        apply()
    }
}
@Composable
fun WelcomeInfOptionsTab (expand: Boolean, icon: Painter, content: @Composable () -> Unit ) {
    var expanded by remember { mutableStateOf(expand) }
    Column (
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier
            .padding(top = 4.dp)
            .clickable { expanded = !expanded }){
        Row {
            Icon(imageVector =
            if (expanded) Icons.Default.ArrowDropDown
            else Icons.Default.KeyboardArrowRight,
                contentDescription = null)
            Icon(painter = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Icon(imageVector =
            if (expanded) Icons.Default.ArrowDropDown
            else Icons.Default.KeyboardArrowLeft,
                contentDescription = null)
        }
        AnimatedVisibility(visible = expanded) {
            content()
        }
        Spacer(modifier = Modifier.size(4.dp))
        Divider()
    }
}

@Composable
fun PartToResults(tabX : InputsAndCalc, icon: Painter, sharedPref: SharedPreferences) {
    var expanded by remember { mutableStateOf(true) }
    Column (
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier
            .padding(top = 4.dp)
            .clickable { expanded = !expanded }){
        Row {
            Icon(imageVector =
            if (expanded) Icons.Default.ArrowDropDown
            else Icons.Default.KeyboardArrowRight,
                contentDescription = null)
            Icon(painter = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Icon(imageVector =
            if (expanded) Icons.Default.ArrowDropDown
            else Icons.Default.KeyboardArrowLeft,
                contentDescription = null)
        }
        AnimatedVisibility(visible = expanded) {
            ResultPart(tabX, sharedPref)
        }
        Spacer(modifier = Modifier.size(4.dp))
        Divider()
    }
}

@Composable
fun OPRobabilities (sharedPref: SharedPreferences) {
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = rememberSaveable { listOf(
        R.drawable.circle_options,
        R.drawable.circle_a,
        R.drawable.circle_b,
        R.drawable.circle_c,
        R.drawable.circle_d,
        R.drawable.circle_abcd,)
    }
    val tabA = remember {InputsAndCalc()}
    val tabB = remember {InputsAndCalc()}
    val tabC = remember {InputsAndCalc()}
    val tabD = remember {InputsAndCalc()}

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { },
        bottomBar = {
            TabRow(
                selectedTabIndex = tabIndex,
                indicator = { },
            ) {
                tabs.forEachIndexed { index, icon ->
                    Tab(icon = {
                        Icon(painter = painterResource(id = icon), contentDescription = null) },
                        selected = index == tabIndex,
                        onClick = { tabIndex = index },
                        modifier = if (index != tabIndex) {
                            Modifier.alpha(0.5f)
                        } else {
                            Modifier
                        }
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
                    0 -> WelcomeInfoOptions(sharedPref)
                    1 -> ProbabilityPage(tabA, sharedPref)
                    2 -> ProbabilityPage(tabB, sharedPref)
                    3 -> ProbabilityPage(tabC, sharedPref)
                    4 -> ProbabilityPage(tabD, sharedPref)
                    else -> {
                        Column (modifier = Modifier.verticalScroll(scrollState)
                        ) {
                            PartToResults(tabA, painterResource(id = R.drawable.circle_a), sharedPref)
                            PartToResults(tabB, painterResource(id = R.drawable.circle_b), sharedPref)
                            PartToResults(tabC, painterResource(id = R.drawable.circle_c), sharedPref)
                            PartToResults(tabD, painterResource(id = R.drawable.circle_d), sharedPref)
                        }
                    }
                }
            }
        }
    )
}

/*
@Preview(showBackground = true)
@Composable
fun OPRobabilitiesPreview() {
    OPRobalitiesTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            OPRobabilities(sharedPref = )
        }
    }
}*/
