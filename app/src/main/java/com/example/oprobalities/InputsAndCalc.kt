package com.example.oprobalities

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf

class InputsAndCalc (
    var attacks : MutableIntState = mutableIntStateOf(10),
    var quality : MutableIntState = mutableIntStateOf(4),
    var attackerModels: MutableIntState = mutableIntStateOf(1),
    var defenderModels: MutableIntState = mutableIntStateOf(1),
    var defenderRules: MutableState<List<Pair<String, Int>>> = mutableStateOf(
        mutableListOf(
            "AP(-0)" to 0,
            "to hit(-0)" to 0,
            "Shield Wall" to 0,
            "Counter (0)" to 0,
            " " to 0,
            " " to 0,
            " " to 0,
            "Regeneration +(0)" to 0,
        )
    ),
    var weaponRules: MutableState<List<Pair<String, Int>>> = mutableStateOf(
        mutableListOf(
            "AP(0)" to 0,
            "to hit(0)" to 0,
            "Reliable/Sniper" to 0,
            "Lock-On" to 0,
            "Blast(0)" to 0,
            "Rending" to 0,
            "Poison" to 0,
            "Regeneration -(0)" to 0,
            "Wound on saving \u2680" to 0, // #8, Unicode dice 1
            "Impact hits AP(0)" to 0,
            "Double hits on ⚅" to 0,
            "Triple hits on ⚅" to 0,
            "disable Regeneration" to 0 // 12
        )
    ),
    var attackerRules: MutableState<List<Pair<String, Int>>> = mutableStateOf(
        mutableListOf(
            "AP(0)" to 0,
            "to hit(0)" to 0,
            "Double hits on \u2685" to 0, //Unicode dice 6
            "Double hits on \u2684" to 0, //Unicode dice 5
            "Extra attack on ⚅" to 0,
            "Extra attack on ⚄" to 0,
            "Two extra attacks on ⚅" to 0,
            "Extra wound on sep. roll" to 0,
            "Impact(0)" to 0,
            "Impact hits AP(0)" to 0,
            "Sergeant" to 0
        )
    )
){
    fun digitRangeOfX (rulesList: MutableState<List<Pair<String, Int>>>, index: Int) : List<Int> {
        return when {
            rulesList.value[index].first.matches("Impact\\(.*\\)".toRegex()) -> (0..50).toList()
            rulesList.value[index].first.matches("Counter\\(.*\\)".toRegex()) -> (0..50).toList()
            rulesList.value[index].first.matches("Blast\\(.*\\)".toRegex()) -> listOf(0, 3, 6, 9, 12)
            else -> (0..4).toList()
        }
    }
    fun combineAPXs (index: Int) : Int { // Xs being the digit in brackets of the rule and the second value of their pairs
        return (attackerRules.value[index].second + weaponRules.value[index].second - defenderRules.value[index].second)
            .coerceIn(-4..4)
    }

    fun combineTwoXs (index: Int) : Int {
        return attackerRules.value[index].second + weaponRules.value[index].second
    }

    fun updateRuleString(
            rulesList : MutableState<List<Pair<String, Int>>>,
            index: Int,
            newDigit: Int) {
        val currentRulesList = rulesList.value.toMutableList()
        currentRulesList[index] = currentRulesList[index].first.replace(Regex("\\d+")) { newDigit.toString() } to newDigit
        rulesList.value = currentRulesList
    }

    fun stringFromTrues (rulesList : MutableState<List<Pair<String, Int>>>) : String {
        return rulesList.value.filter{ it.second > 0}.joinToString { it.first }
    }

    fun hitChance () : Float {
        var hitModifier = 0 //if Lock-On == 1 stay 0
        if (weaponRules.value[3].second == 0) hitModifier = combineAPXs(1)

        var qualityOrTwoPlus = quality.intValue //if Sniper/Reliable == 1 set quality to 2
        if (weaponRules.value[2].second == 1) qualityOrTwoPlus = 2

        return (7+hitModifier-qualityOrTwoPlus.toFloat())/6
    }

    fun sgtHitChance () : Float {
        var hitModifier = 0 //if Lock-On == 1 stay 0
        if (weaponRules.value[3].second == 0) hitModifier = combineAPXs(1)

        var qualityOrTwoPlus = (quality.intValue-1).coerceIn(2..6) //if Sniper/Reliable == 1 set quality to 2
        if (weaponRules.value[2].second == 1) qualityOrTwoPlus = 2

        return (7+hitModifier-qualityOrTwoPlus.toFloat())/6
    }

    fun naturalSixes () : Float {
        val basic = attacks.intValue.toFloat()/6
        val extraRollSixes = ((attackerRules.value[4].second)+(attackerRules.value[5].second)+((attackerRules.value[6].second)*2)).toFloat()
        return basic + (basic*(extraRollSixes/6))
    }

    fun averageHits () : Float {
        val blastHits = //multiply hit, but keep defenderModels in mind!
            weaponRules.value[4].second.coerceIn(1..15) - ( weaponRules.value[4].second-defenderModels.value).coerceIn(0..15)

        val sgtAttacks = (attacks.value/attackerModels.value)
        val sgtSixes = naturalSixes() - naturalSixes() / attackerModels.intValue
        val sgtExtraHits =
            (sgtSixes)*(attackerRules.value[2].second+attackerRules.value[3].second+weaponRules.value[10].second+(weaponRules.value[11].second*2))
        val sgtExtraAttacks =
            (sgtAttacks*((attackerRules.value[4].second)+(attackerRules.value[5].second)+((attackerRules.value[6].second)*2)).toFloat())/6
        val sgtAverageHits =
            (sgtHitChance() * (sgtAttacks + sgtExtraAttacks)) * blastHits + sgtExtraHits

        val extraHits = //6s * every multiplication mod == 1
            (naturalSixes())*(attackerRules.value[2].second+attackerRules.value[3].second+weaponRules.value[10].second+(weaponRules.value[11].second*2))
        val extraAttacks = //roll a new dice rules
            (attacks.intValue*((attackerRules.value[4].second)+(attackerRules.value[5].second)+((attackerRules.value[6].second)*2)).toFloat())/6

        val coefficient = (attackerModels.value.toFloat()-1)/attackerModels.value.toFloat()

        val averageHits = (hitChance() * ((attacks.intValue) + extraAttacks)) * blastHits + extraHits
        val averageHitsWithoutSgt = averageHits * coefficient
        val averageHitsInclSgt = sgtAverageHits + averageHitsWithoutSgt

        return if (attackerRules.value[10].second == 1) {averageHitsInclSgt}
        else averageHits
    }

    fun impactHits () : Float { // hit on a 2+
        val impactsMinusCounter = attackerRules.value[8].second - defenderRules.value[3].second
        return (impactsMinusCounter.toFloat())*5/6
    }

    fun disableRegen () : Int {
        return  weaponRules.value[5].second + weaponRules.value[6].second + weaponRules.value[12].second

    }
    fun regenerationChance (base: Int) : Float { // 4/6 modified by poison, rending, regeneration +/-, has to cap 2..6
        return if (disableRegen() == 0) ( base - weaponRules.value[7].second ).toFloat() / 6
        else 1f
    }

    fun poisonReRolls () : Float {
        return averageHits() * 1/6 * weaponRules.value[6].second
    }

    fun withoutRendingHits () : Float { //only natural 6s are considered Rending
        val extraAttackRendingHits = (attacks.intValue * ((attackerRules.value[4].second) + (attackerRules.value[5].second)+((attackerRules.value[6].second)*2)).toFloat())/36
        return if (extraAttackRendingHits >= 1) averageHits() - (attacks.value * 1/6) - extraAttackRendingHits
        else averageHits() - (attacks.value.toFloat() * 1/6)
    }
    fun rendingHits () : Float {
        val extraAttackRendingHits = (attacks.intValue*((attackerRules.value[4].second)+(attackerRules.value[5].second)+((attackerRules.value[6].second)*2)).toFloat())/36
        return if (extraAttackRendingHits >= 1) attacks.value.toFloat()*1/6+extraAttackRendingHits
        else attacks.value.toFloat()*1/6
    }

    fun woundOnSepRoll () : Float {
        return if (attackerRules.value[7].second == 1) {
            attackerModels.value.toFloat() / 6
        } else 0f
    }
    fun woundOnSavingOne () : Float {
        return if (weaponRules.value[8].second == 1) {
            averageHits() * 1/6
        } else 0f
    }

    fun extraWounds () : Float {
        return woundOnSavingOne() + woundOnSepRoll() + poisonReRolls()
    }
    fun extraWoundsString () : String {
        return if (extraWounds() > 0 ) {"; %.2f additional wounds.".format(extraWounds())
        } else "."
    }

    fun reset () {
        attacks = mutableIntStateOf(10)
        quality = mutableIntStateOf(4)
        attackerModels = mutableIntStateOf(1)
        defenderModels = mutableIntStateOf(1)
        defenderRules = mutableStateOf(
            mutableListOf(
                "AP(-0)" to 0,
                "to hit(-0)" to 0,
                "Shield Wall" to 0,
                "Counter (0)" to 0,
                " " to 0,
                " " to 0,
                " " to 0,
                "Regeneration +(0)" to 0,
            )
        )
        weaponRules = mutableStateOf(
            mutableListOf(
                "AP(0)" to 0,
                "to hit(0)" to 0,
                "Reliable/Sniper" to 0,
                "Lock-On" to 0,
                "Blast(0)" to 0,
                "Rending" to 0,
                "Poison" to 0,
                "Regeneration -(0)" to 0,
                "Wound on saving" to 0,
                "Impact hits AP(0)" to 0,
                "Double hits on ⚅" to 0,
                "Triple hits on ⚅" to 0,
                "disable Regeneration" to 0
            )
        )
        attackerRules = mutableStateOf(
            mutableListOf(
                "AP(0)" to 0,
                "to hit(0)" to 0,
                "Double hits on" to 0,
                "Double hits on" to 0,
                "Extra attack on ⚅" to 0,
                "Extra attack on ⚄" to 0,
                "Two extra attacks on ⚅" to 0,
                "Extra wound on sep. roll" to 0,
                "Impact(0)" to 0,
                "Impact hits AP(0)" to 0,
                "Sergeant" to 0
            )
        )

    }
}
