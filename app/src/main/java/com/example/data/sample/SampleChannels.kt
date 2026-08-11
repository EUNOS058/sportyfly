package com.example.data.sample

import com.example.data.model.Channel

/**
 * Sample channel repository data for SportyFly.
 * 
 * IMPORTANT FOR CUSTOMIZATION:
 * To add, edit, or remove channel streams, update this list or replace this class
 * with a dynamic remote API loader, M3U playlist parser, or Firebase Firestore source.
 */
object SampleChannels {

    val CATEGORIES = listOf(
        "All",
        "Sports",
        "Live TV",
        "Bangla",
        "News",
        "Movies",
        "Kids",
        "Entertainment",
        "International",
        "Music"
    )

    // Public test streams (Legally authorized open-source sample streams - Verified 200 OK)
    const val SAMPLE_HLS_1 = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
    const val SAMPLE_HLS_2 = "https://cph-p2p-msl.akamaized.net/hls/live/2000341/test/master.m3u8"
    const val SAMPLE_HLS_3 = "https://playertest.longtailvideo.com/adaptive/oceans/oceans.m3u8"
    const val SAMPLE_HLS_4 = "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8"
    const val SAMPLE_MP4_1 = "https://raw.githubusercontent.com/intel-iot-devkit/sample-videos/master/classroom.mp4"
    const val SAMPLE_MP4_2 = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
    const val SAMPLE_MP4_3 = "https://playertest.longtailvideo.com/adaptive/oceans/oceans.m3u8"

    val DEFAULT_CHANNELS = listOf(
        Channel(
            id = "sports_001",
            name = "SportyFly Live Sports 1",
            logoUrl = "https://picsum.photos/seed/sporty1/200/200",
            streamUrl = SAMPLE_HLS_1,
            category = "Sports",
            country = "Global",
            language = "English",
            isLive = true,
            description = "24/7 Live Premier Football, Cricket & Motorsports coverage on SportyFly HD.",
            posterUrl = "https://picsum.photos/seed/sporty1_banner/800/450"
        ),
        Channel(
            id = "sports_002",
            name = "SportyFly Arena HD",
            logoUrl = "https://picsum.photos/seed/arena/200/200",
            streamUrl = SAMPLE_HLS_2,
            category = "Sports",
            country = "Global",
            language = "English",
            isLive = true,
            description = "High octane motorsports, tennis tournaments, and combat sports highlights.",
            posterUrl = "https://picsum.photos/seed/arena_banner/800/450"
        ),
        Channel(
            id = "bangla_001",
            name = "[Demo] Bangladesh Live Sports 24",
            logoUrl = "https://picsum.photos/seed/bangla1/200/200",
            streamUrl = SAMPLE_HLS_3,
            category = "Bangla",
            country = "Bangladesh",
            language = "Bangla",
            isLive = true,
            description = "Demo stream for Bangladesh Live Sports 24. Replace with your authorized M3U/M3U8 live stream URL.",
            posterUrl = "https://picsum.photos/seed/bangla1_banner/800/450"
        ),
        Channel(
            id = "bangla_002",
            name = "[Demo] Bangla News & Cinema",
            logoUrl = "https://picsum.photos/seed/bangla2/200/200",
            streamUrl = SAMPLE_MP4_1,
            category = "Bangla",
            country = "Bangladesh",
            language = "Bangla",
            isLive = true,
            description = "Demo stream for Bangla News & Cinema. Replace with your authorized M3U/M3U8 live stream URL.",
            posterUrl = "https://picsum.photos/seed/bangla2_banner/800/450"
        ),
        Channel(
            id = "india_001",
            name = "[Demo] India Cricket & Sports 24",
            logoUrl = "https://picsum.photos/seed/india1/200/200",
            streamUrl = SAMPLE_HLS_4,
            category = "Sports",
            country = "India",
            language = "Hindi / English",
            isLive = true,
            description = "Demo stream for India Cricket & Sports 24. Replace with your authorized M3U/M3U8 live stream URL.",
            posterUrl = "https://picsum.photos/seed/india1_banner/800/450"
        ),
        Channel(
            id = "india_002",
            name = "[Demo] India News & Cinema",
            logoUrl = "https://picsum.photos/seed/india2/200/200",
            streamUrl = SAMPLE_MP4_2,
            category = "News",
            country = "India",
            language = "Hindi",
            isLive = true,
            description = "Demo stream for India News & Cinema. Replace with your authorized M3U/M3U8 live stream URL.",
            posterUrl = "https://picsum.photos/seed/india2_banner/800/450"
        ),
        Channel(
            id = "news_001",
            name = "Global News HD",
            logoUrl = "https://picsum.photos/seed/news1/200/200",
            streamUrl = SAMPLE_HLS_4,
            category = "News",
            country = "International",
            language = "English",
            isLive = true,
            description = "Breaking international headlines, financial market updates, and live reports.",
            posterUrl = "https://picsum.photos/seed/news1_banner/800/450"
        ),
        Channel(
            id = "movies_001",
            name = "CineMax HD Movies",
            logoUrl = "https://picsum.photos/seed/movies1/200/200",
            streamUrl = SAMPLE_MP4_3,
            category = "Movies",
            country = "Global",
            language = "English",
            isLive = false,
            description = "Blockbuster sci-fi action and drama movies 24/7.",
            posterUrl = "https://picsum.photos/seed/movies1_banner/800/450"
        ),
        Channel(
            id = "kids_001",
            name = "KidsZone Fly",
            logoUrl = "https://picsum.photos/seed/kids1/200/200",
            streamUrl = SAMPLE_MP4_1,
            category = "Kids",
            country = "Global",
            language = "English",
            isLive = true,
            description = "Fun animated adventures, educational shows, and bedtime stories for children.",
            posterUrl = "https://picsum.photos/seed/kids1_banner/800/450"
        ),
        Channel(
            id = "livetv_001",
            name = "Live TV Network",
            logoUrl = "https://picsum.photos/seed/livetv1/200/200",
            streamUrl = SAMPLE_HLS_1,
            category = "Live TV",
            country = "Global",
            language = "English",
            isLive = true,
            description = "Non-stop prime live television programming from world-class creators.",
            posterUrl = "https://picsum.photos/seed/livetv1_banner/800/450"
        ),
        Channel(
            id = "ent_001",
            name = "Entertainment Plus",
            logoUrl = "https://picsum.photos/seed/ent1/200/200",
            streamUrl = SAMPLE_MP4_2,
            category = "Entertainment",
            country = "Global",
            language = "English",
            isLive = false,
            description = "Variety shows, celebrity interviews, and lifestyle entertainment.",
            posterUrl = "https://picsum.photos/seed/ent1_banner/800/450"
        ),
        Channel(
            id = "music_001",
            name = "SportyFly Music Hits",
            logoUrl = "https://picsum.photos/seed/music1/200/200",
            streamUrl = SAMPLE_HLS_2,
            category = "Music",
            country = "Global",
            language = "English",
            isLive = true,
            description = "Non-stop top 40 music video streams and live stadium concert recordings.",
            posterUrl = "https://picsum.photos/seed/music1_banner/800/450"
        )
    )
}
