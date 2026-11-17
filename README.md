# Kobita Archive — Bengali Poem Dataset Android App

An offline Android application built with Kotlin and Jetpack Compose for reading, exploring, and analyzing the **Bengali Poem Dataset**.

## Features
- **Explore Anthology**: Browse 332 full-text Bengali poems across 14 literary classes (*মানবতাবাদী*, *প্রেমমূলক*, *চিন্তামূলক*, *স্বদেশমূলক*, and more) with live Bengali/English search, genre chips, sorting, daily featured verse, and surprise poem discovery.
- **Poets Directory & Profiles**: Explore all 30 Bengali poets in the corpus with per-poet stylometric profiles, genre distribution bars, and collected works.
- **Stylometric Insights**: Corpus-wide quantitative analysis including total word & unique vocabulary counts, interactive literary class distribution bars, poet Type-Token Ratio (TTR) and volume leaderboards, and link to the NLPIR 2022 research paper.
- **Distraction-Free Poem Reader**: Bundled `Noto Serif Bengali` and `Noto Sans Bengali` typography, adjustable text size (14–26 sp), Paper/Sepia/Night surface modes, per-poem stylometric breakdown with top frequent words, archival provenance links, clipboard copy, and system share.
- **Personal Library**: Persistent bookmarks, reading history, and display preferences backed by Jetpack DataStore.

## Dataset Attribution
Built on the **Bengali Poem Dataset** (originally published at [NLPIR 2022](https://shuhanmirza.com/files/NL069_NLPIR.pdf)), preserving the bundled textual corpus under `dataset/` and `LICENSE`.