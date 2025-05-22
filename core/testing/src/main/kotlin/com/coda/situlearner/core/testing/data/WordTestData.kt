package com.coda.situlearner.core.testing.data

import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordContext
import com.coda.situlearner.core.model.data.WordContextView
import com.coda.situlearner.core.model.data.WordMeaning
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.data.WordWithContexts
import kotlinx.datetime.Instant

val wordsTestData = listOf(
    Word(
        id = "0",
        word = "run",
        language = Language.English,
        dictionaryName = "youdao",
        pronunciation = "/ rʌn /",
        meanings = listOf(
            WordMeaning(
                partOfSpeechTag = "n",
                definition = "跑步，赛跑；旅程，航程；一系列（成功或失败）；连续上演（或放映）；尝试，努力；额定产量；抛售（美元、英镑等）；争购，挤兑；滑道，路径；（板球或棒球中的）得分；使用自由，出入自由（the run of）；竞选；普通人，普通事物；饲养场；急奏，走句；顺子；<非正式>腹泻（the runs）；<美>（长统袜或连裤袜的）抽丝；（油漆或类似物刷得过厚引起的）挂流，小溪；（航海）船尾端部"
            ),
            WordMeaning(
                partOfSpeechTag = "v",
                definition = "跑，奔跑；参加（赛跑），举行（比赛）；跑垒，持球跑动进攻；奔忙，赶快去；管理，经营，使用（车辆）；（使）运转，操作；刊登，播放；（使）行驶；（使）移动，揉擦；闯红灯；（使）流动，流淌；掉色，渗色；变成，变得；达到（一定数量或比率）；（对……）进行（测试或检验）；参加竞选；（连裤袜、长统袜）抽丝，脱线；（使）延伸；（感觉或想法）掠过，迅速传遍；包含（某种词语、内容等）；持续，延续；偷运，走私；印刷；（特征）共有，世代相传（run in）；<美>（物品，行动）花费（某人）（特定数额的钱）"
            )
        ),
        lastViewedDate = Instant.parse("2021-10-31T00:00:00.000Z"),
        meaningProficiency = WordProficiency.Beginner
    ),
    Word(
        id = "1",
        word = "actively",
        language = Language.English,
        dictionaryName = null,
        pronunciation = null,
        meanings = emptyList(),
        lastViewedDate = null,
        meaningProficiency = WordProficiency.Unset,
    ),
    Word(
        id = "2",
        word = "hello",
        language = Language.English,
        dictionaryName = "iciba",
        pronunciation = "[həˈləʊ]",
        meanings = listOf(
            WordMeaning(
                partOfSpeechTag = "int.",
                definition = "哈喽，喂; 你好，您好; 表示问候; 打招呼"
            ),
            WordMeaning(
                partOfSpeechTag = "n.",
                definition = "“喂”的招呼声或问候声"
            )
        ),
        lastViewedDate = Instant.parse("2021-10-29T00:00:00.000Z"),
        meaningProficiency = WordProficiency.Intermediate
    ),
    Word(
        id = "3",
        word = "unconstitutional",
        language = Language.English,
        dictionaryName = null,
        pronunciation = null,
        meanings = emptyList(),
        lastViewedDate = Instant.parse("2021-10-20T00:00:00.000Z"),
        meaningProficiency = WordProficiency.Intermediate
    ),
    Word(
        id = "4",
        word = "ok",
        language = Language.English,
        dictionaryName = null,
        pronunciation = null,
        meanings = emptyList(),
        lastViewedDate = Instant.parse("2021-10-01T00:00:00.000Z"),
        meaningProficiency = WordProficiency.Proficient,
    ),
    Word(
        id = "5",
        word = "これ",
        language = Language.Japanese,
        dictionaryName = "youdao",
        pronunciation = "これ⓪",
        meanings = listOf(
            WordMeaning(
                partOfSpeechTag = "[代词]",
                definition = "（离说话人最近的人、事物、场所的用语）这，此。\n" +
                        "现在。\n" +
                        "（谈话者眼前提到的话题）这。\n" +
                        "（对别人说自己身旁的家人）这人，此人。\n" +
                        "〈与「維」、「惟」同义〉（在汉文格调的文章中，用于调整语调的词语）惟。\n" +
                        "这；本；这个；呸；咄；斯；此\n" +
                        "（用これという的形式表示）值得一提的，特别的"
            )
        ),
        lastViewedDate = Instant.parse("2021-10-10T00:00:00.000Z"),
        meaningProficiency = WordProficiency.Intermediate
    ),
    Word(
        id = "6",
        word = "綺麗",
        language = Language.Japanese,
        dictionaryName = null,
        pronunciation = null,
        meanings = emptyList(),
        lastViewedDate = Instant.parse("2021-10-31T12:00:00.000Z"),
        meaningProficiency = WordProficiency.Unset,
    )
)

val wordContextsTestData = listOf(
    WordContext(
        id = "0",
        wordId = "0",
        mediaId = "0",
        createdDate = Instant.parse("2021-10-30T12:00:00.000Z"),
        subtitleStartTimeInMs = 0L,
        subtitleEndTimeInMs = 5000L,
        subtitleSourceText = "This is a good run.",
        subtitleTargetText = "这是一次很好的运行",
        wordStartIndex = 15,
        wordEndIndex = 18,
    ),
    WordContext(
        id = "1",
        wordId = "0",
        mediaId = "0",
        createdDate = Instant.parse("2021-10-30T13:00:00.000Z"),
        subtitleStartTimeInMs = 10000L,
        subtitleEndTimeInMs = 15000L,
        subtitleSourceText = "Hey, run fast! This baster is gonna bite you! Oh, no no no no no no...",
        subtitleTargetText = null,
        wordStartIndex = 5,
        wordEndIndex = 8
    ),
    WordContext(
        id = "2",
        wordId = "1",
        mediaId = "1",
        createdDate = Instant.parse("2021-10-20T13:00:00.000Z"),
        subtitleStartTimeInMs = 0L,
        subtitleEndTimeInMs = 5000L,
        subtitleSourceText = "It actively makes sound.",
        subtitleTargetText = null,
        wordStartIndex = 3,
        wordEndIndex = 11
    ),
    WordContext(
        id = "3",
        wordId = "2",
        mediaId = "2",
        createdDate = Instant.parse("2021-10-19T13:00:00.000Z"),
        subtitleStartTimeInMs = 50000L,
        subtitleEndTimeInMs = 55000L,
        subtitleSourceText = "hello?",
        subtitleTargetText = null,
        wordStartIndex = 0,
        wordEndIndex = 5,
    ),
    WordContext(
        id = "4",
        wordId = "3",
        mediaId = "2",
        createdDate = Instant.parse("2021-10-19T13:00:00.000Z"),
        subtitleStartTimeInMs = 0L,
        subtitleEndTimeInMs = 5000L,
        subtitleSourceText = "This is an unconstitutional behavior",
        subtitleTargetText = null,
        wordStartIndex = 11,
        wordEndIndex = 27,
    ),
    WordContext(
        id = "5",
        wordId = "5",
        mediaId = "1",
        createdDate = Instant.parse("2021-10-17T13:00:00.000Z"),
        subtitleStartTimeInMs = 18000L,
        subtitleEndTimeInMs = 23000L,
        subtitleSourceText = "えと、これ？",
        subtitleTargetText = null,
        wordStartIndex = 3,
        wordEndIndex = 5,
    ),
    WordContext(
        id = "6",
        wordId = "6",
        mediaId = null,
        createdDate = Instant.parse("2021-10-15T13:00:00.000Z"),
        subtitleStartTimeInMs = 18000L,
        subtitleEndTimeInMs = 23000L,
        subtitleSourceText = "君はきれいだ",
        subtitleTargetText = null,
        wordStartIndex = 2,
        wordEndIndex = 5,
    ),
)

val wordWithContextsListTestData: List<WordWithContexts>
    get() {
        val idToFile = mediaFilesTestData.associateBy { it.id }
        val idToCollection = mediaCollectionsTestData.associateBy { it.id }
        val contextsByWordId = wordContextsTestData.groupBy { it.wordId }

        return wordsTestData.map { word ->
            val wordContexts = contextsByWordId[word.id]?.map { context ->
                val mediaFile = idToFile[context.mediaId]
                val mediaGroup = mediaFile?.let { idToCollection[it.collectionId] }
                WordContextView(
                    wordContext = context,
                    mediaFile = mediaFile,
                    mediaCollection = mediaGroup
                )
            } ?: emptyList()

            WordWithContexts(
                word = word,
                contexts = wordContexts
            )
        }
    }