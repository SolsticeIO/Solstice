package urstark.solstice.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

object SolarIcons {

    private var _CloudDone: ImageVector? = null
    val CloudDone: ImageVector
        get() {
            if (_CloudDone != null) {
                return _CloudDone!!
            }
            _CloudDone = Builder(
                name = "CloudDone",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f
            ).apply {

                addPath(
                    pathData = addPathNodes("M21.0672 11.8568L20.4253 11.469L20.4253 11.469L21.0672 11.8568ZM15.5 14.25C15.0858 14.25 14.75 14.5858 14.75 15C14.75 15.4142 15.0858 15.75 15.5 15.75V14.25ZM8.25 8.5C8.25 8.91421 8.58579 9.25 9 9.25C9.41421 9.25 9.75 8.91421 9.75 8.5H8.25ZM12.1432 2.93276L11.7553 2.29085L11.7553 2.29085L12.1432 2.93276ZM1.74227 15.2247C1.86638 15.6199 2.28736 15.8397 2.68254 15.7155C3.07772 15.5914 3.29746 15.1704 3.17334 14.7753L1.74227 15.2247ZM16.6245 20.013C16.2659 20.2204 16.1434 20.6792 16.3508 21.0377C16.5582 21.3963 17.017 21.5188 17.3755 21.3114L16.6245 20.013ZM3.98703 7.37554C4.19443 7.017 4.07191 6.5582 3.71337 6.3508C3.35482 6.14339 2.89602 6.26591 2.68862 6.62446L3.98703 7.37554ZM6.62446 2.68862C6.26591 2.89602 6.14339 3.35482 6.3508 3.71337C6.5582 4.07191 7.017 4.19443 7.37554 3.98703L6.62446 2.68862ZM20.4253 11.469C19.4172 13.1373 17.5882 14.25 15.5 14.25V15.75C18.1349 15.75 20.4407 14.3439 21.7092 12.2447L20.4253 11.469ZM9.75 8.5C9.75 6.41182 10.8627 4.5828 12.531 3.57467L11.7553 2.29085C9.65609 3.5593 8.25 5.86509 8.25 8.5H9.75ZM3.17334 14.7753C2.89847 13.9001 2.75 12.9681 2.75 12H1.25C1.25 13.1223 1.42224 14.2058 1.74227 15.2247L3.17334 14.7753ZM21.25 12C21.25 15.4229 19.3912 18.4125 16.6245 20.013L17.3755 21.3114C20.5868 19.4538 22.75 15.98 22.75 12H21.25ZM12 2.75C11.9115 2.75 11.8077 2.71008 11.7324 2.63168C11.6686 2.56527 11.6538 2.50244 11.6503 2.47703C11.6461 2.44587 11.6482 2.35557 11.7553 2.29085L12.531 3.57467C13.0342 3.27065 13.196 2.71398 13.1368 2.27627C13.0754 1.82126 12.7166 1.25 12 1.25V2.75ZM21.7091 12.2447C21.6444 12.3518 21.5541 12.3539 21.523 12.3497C21.4976 12.3462 21.4347 12.3314 21.3683 12.2676C21.2899 12.1923 21.25 12.0885 21.25 12H22.75C22.75 11.2834 22.1787 10.9246 21.7237 10.8632C21.286 10.804 20.7293 10.9658 20.4253 11.469L21.7091 12.2447ZM2.75 12C2.75 10.3139 3.20043 8.73533 3.98703 7.37554L2.68862 6.62446C1.77351 8.2064 1.25 10.0432 1.25 12H2.75ZM7.37554 3.98703C8.73533 3.20043 10.3139 2.75 12 2.75V1.25C10.0432 1.25 8.2064 1.77351 6.62446 2.68862L7.37554 3.98703Z"),
                    fill = SolidColor(Color.White),
                    stroke = null,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M10.0476 15.142C10.4349 15.0119 10.8516 14.9412 11.2857 14.9412C11.7113 14.9412 12.1201 15.0092 12.5008 15.1344M5.3255 16.7555C5.15087 16.723 4.97039 16.7059 4.78571 16.7059C3.24721 16.7059 2 17.891 2 19.3529C2 20.8149 3.24721 22 4.78571 22H11.2857C13.3371 22 15 20.4198 15 18.4706C15 16.9257 13.9554 15.6126 12.5008 15.1344M5.3255 16.7555C5.17659 16.3736 5.09524 15.9605 5.09524 15.5294C5.09524 13.5802 6.75818 12 8.80952 12C10.7203 12 12.2941 13.3711 12.5008 15.1344M5.3255 16.7555C5.69238 16.824 6.03343 16.9609 6.33333 17.1516"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Miter
                )
            }.build()
            return _CloudDone!!
        }

    private var _CloudOff: ImageVector? = null
    val CloudOff: ImageVector
        get() {
            if (_CloudOff != null) {
                return _CloudOff!!
            }
            _CloudOff = Builder(
                name = "CloudOff",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f
            ).apply {

                addPath(
                    pathData = addPathNodes("M22 14.3529C22 17.4717 19.4416 20 16.2857 20H11M14.381 9.02721C14.9767 8.81911 15.6178 8.70588 16.2857 8.70588C16.9404 8.70588 17.5693 8.81468 18.1551 9.01498M7.11616 11.6089C6.8475 11.5567 6.56983 11.5294 6.28571 11.5294C3.91878 11.5294 2 13.4256 2 15.7647C2 18.1038 3.91878 20 6.28571 20H7M7.11616 11.6089C6.88706 10.9978 6.7619 10.3369 6.7619 9.64706C6.7619 6.52827 9.32028 4 12.4762 4C15.4159 4 17.8371 6.19371 18.1551 9.01498M7.11616 11.6089C7.68059 11.7184 8.20528 11.9374 8.66667 12.2426M18.1551 9.01498C18.8381 9.24853 19.4623 9.60648 20 10.0614"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Miter
                )
            }.build()
            return _CloudOff!!
        }

    private var _Album: ImageVector? = null
    val Album: ImageVector
        get() {
            if (_Album != null) {
                return _Album!!
            }
            _Album = Builder(
                name = "Album",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f
            ).apply {

                addPath(
                    pathData = addPathNodes("M21.1935 16.793C20.8437 19.2739 20.6689 20.5143 19.7717 21.2572C18.8745 22 17.5512 22 14.9046 22H9.09536C6.44881 22 5.12553 22 4.22834 21.2572C3.33115 20.5143 3.15626 19.2739 2.80648 16.793L2.38351 13.793C1.93748 10.6294 1.71447 9.04765 2.66232 8.02383C3.61017 7 5.29758 7 8.67239 7H15.3276C18.7024 7 20.3898 7 21.3377 8.02383C22.0865 8.83268 22.1045 9.98979 21.8592 12"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M19.5617 7C19.7904 5.69523 18.7863 4.5 17.4617 4.5H6.53788C5.21323 4.5 4.20922 5.69523 4.43784 7"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M17.4999 4.5C17.5283 4.24092 17.5425 4.11135 17.5427 4.00435C17.545 2.98072 16.7739 2.12064 15.7561 2.01142C15.6497 2 15.5194 2 15.2588 2H8.74099C8.48035 2 8.35002 2 8.24362 2.01142C7.22584 2.12064 6.45481 2.98072 6.45704 4.00434C6.45727 4.11135 6.47146 4.2409 6.49983 4.5"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M 16.5 10.0 A 1.5 1.5 0 1 0 16.5 13.0 A 1.5 1.5 0 1 0 16.5 10.0 Z"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M19.9999 20L17.1157 17.8514C16.1856 17.1586 14.8004 17.0896 13.7766 17.6851L13.5098 17.8403C12.7984 18.2542 11.8304 18.1848 11.2156 17.6758L7.37738 14.4989C6.6113 13.8648 5.38245 13.8309 4.5671 14.4214L3.24316 15.3803"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Miter
                )
            }.build()
            return _Album!!
        }

    private var _Hearing: ImageVector? = null
    val Hearing: ImageVector
        get() {
            if (_Hearing != null) {
                return _Hearing!!
            }
            _Hearing = Builder(
                name = "Hearing",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f
            ).apply {

                addPath(
                    pathData = addPathNodes("M8.96173 18.9111L9.42605 18.3221L8.96173 18.9111ZM12 5.50088L11.4596 6.02098C11.601 6.16787 11.7961 6.25088 12 6.25088C12.2039 6.25088 12.399 6.16787 12.5404 6.02098L12 5.50088ZM15.0383 18.9111L15.5026 19.5001L15.0383 18.9111ZM7.00061 16.4211C6.68078 16.1579 6.20813 16.2038 5.94491 16.5236C5.68169 16.8435 5.72758 17.3161 6.04741 17.5793L7.00061 16.4211ZM2.34199 13.4117C2.54074 13.7751 2.99647 13.9086 3.35988 13.7098C3.7233 13.5111 3.85677 13.0554 3.65801 12.6919L2.34199 13.4117ZM2.75 9.13734C2.75 6.98647 3.96537 5.18277 5.62436 4.42444C7.23607 3.68772 9.40166 3.88282 11.4596 6.02098L12.5404 4.98078C10.0985 2.44377 7.26409 2.02563 5.00076 3.0602C2.78471 4.07317 1.25 6.42527 1.25 9.13734H2.75ZM8.49742 19.5001C9.00965 19.9039 9.55954 20.3345 10.1168 20.6602C10.6739 20.9857 11.3096 21.2502 12 21.2502V19.7502C11.6904 19.7502 11.3261 19.6295 10.8736 19.3651C10.4213 19.1008 9.95208 18.7368 9.42605 18.3221L8.49742 19.5001ZM15.5026 19.5001C16.9292 18.3755 18.7528 17.0868 20.1833 15.476C21.6395 13.8363 22.75 11.8029 22.75 9.13734H21.25C21.25 11.3347 20.3508 13.0285 19.0617 14.48C17.7469 15.9605 16.0896 17.1273 14.574 18.3221L15.5026 19.5001ZM22.75 9.13734C22.75 6.42527 21.2153 4.07317 18.9992 3.0602C16.7359 2.02563 13.9015 2.44377 11.4596 4.98078L12.5404 6.02098C14.5983 3.88282 16.7639 3.68772 18.3756 4.42444C20.0346 5.18277 21.25 6.98647 21.25 9.13734H22.75ZM14.574 18.3221C14.0479 18.7368 13.5787 19.1008 13.1264 19.3651C12.6739 19.6295 12.3096 19.7502 12 19.7502V21.2502C12.6904 21.2502 13.3261 20.9857 13.8832 20.6602C14.4405 20.3345 14.9903 19.9039 15.5026 19.5001L14.574 18.3221ZM9.42605 18.3221C8.63014 17.6947 7.82129 17.0966 7.00061 16.4211L6.04741 17.5793C6.87768 18.2627 7.75472 18.9146 8.49742 19.5001L9.42605 18.3221ZM3.65801 12.6919C3.0968 11.6658 2.75 10.5035 2.75 9.13734H1.25C1.25 10.7749 1.66995 12.183 2.34199 13.4117L3.65801 12.6919Z"),
                    fill = SolidColor(Color.White),
                    stroke = null,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
            }.build()
            return _Hearing!!
        }

    private var _LibraryMusic: ImageVector? = null
    val LibraryMusic: ImageVector
        get() {
            if (_LibraryMusic != null) {
                return _LibraryMusic!!
            }
            _LibraryMusic = Builder(
                name = "LibraryMusic",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f
            ).apply {

                addPath(
                    pathData = addPathNodes("M12 17C12 17.8284 11.3284 18.5 10.5 18.5C9.67157 18.5 9 17.8284 9 17C9 16.1716 9.67157 15.5 10.5 15.5C11.3284 15.5 12 16.1716 12 17ZM12 17V10.5C12 12.1569 13.8954 13.5 15 13.5"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                )
                addPath(
                    pathData = addPathNodes("M19.5617 7C19.7904 5.69523 18.7863 4.5 17.4617 4.5H6.53788C5.21323 4.5 4.20922 5.69523 4.43784 7"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M17.4999 4.5C17.5283 4.24092 17.5425 4.11135 17.5427 4.00435C17.545 2.98072 16.7739 2.12064 15.7561 2.01142C15.6497 2 15.5194 2 15.2588 2H8.74099C8.48035 2 8.35002 2 8.24362 2.01142C7.22584 2.12064 6.45481 2.98072 6.45704 4.00434C6.45727 4.11135 6.47146 4.2409 6.49983 4.5"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M21.1935 16.793C20.8437 19.2739 20.6689 20.5143 19.7717 21.2572C18.8745 22 17.5512 22 14.9046 22H9.09536C6.44881 22 5.12553 22 4.22834 21.2572C3.33115 20.5143 3.15626 19.2739 2.80648 16.793L2.38351 13.793C1.93748 10.6294 1.71447 9.04765 2.66232 8.02383C3.61017 7 5.29758 7 8.67239 7H15.3276C18.7024 7 20.3898 7 21.3377 8.02383C22.0865 8.83268 22.1045 9.98979 21.8592 12"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Miter
                )
            }.build()
            return _LibraryMusic!!
        }

    private var _MusicNote: ImageVector? = null
    val MusicNote: ImageVector
        get() {
            if (_MusicNote != null) {
                return _MusicNote!!
            }
            _MusicNote = Builder(
                name = "MusicNote",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f
            ).apply {

                addPath(
                    pathData = addPathNodes("M9 19C9 20.6569 7.65685 22 6 22C4.34315 22 3 20.6569 3 19C3 17.3431 4.34315 16 6 16C7.65685 16 9 17.3431 9 19Z"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M21 17C21 18.6569 19.6569 20 18 20C16.3431 20 15 18.6569 15 17C15 15.3431 16.3431 14 18 14C19.6569 14 21 15.3431 21 17Z"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M9 19V8"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M20.25 11.5C20.25 11.9142 20.5858 12.25 21 12.25C21.4142 12.25 21.75 11.9142 21.75 11.5H20.25ZM21.75 11.5V6H20.25V11.5H21.75Z"),
                    fill = SolidColor(Color.White),
                    stroke = null,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M15.7351 3.75466L11.7351 5.08799C10.4151 5.52801 9.75503 5.74801 9.37752 6.27179C9 6.79556 9 7.49128 9 8.88273V11.9997L21 7.99969V7.54939C21 5.01693 21 3.7507 20.1694 3.15206C19.3388 2.55341 18.1376 2.95383 15.7351 3.75466Z"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Miter
                )
            }.build()
            return _MusicNote!!
        }

    private var _PersonAdd: ImageVector? = null
    val PersonAdd: ImageVector
        get() {
            if (_PersonAdd != null) {
                return _PersonAdd!!
            }
            _PersonAdd = Builder(
                name = "PersonAdd",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f
            ).apply {

                addPath(
                    pathData = addPathNodes("M15 13H9"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M12 10L12 16"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M19 10H18"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M2 13.3636C2 10.2994 2 8.76721 2.74902 7.6666C3.07328 7.19014 3.48995 6.78104 3.97524 6.46268C4.69555 5.99013 5.59733 5.82123 6.978 5.76086C7.63685 5.76086 8.20412 5.27068 8.33333 4.63636C8.52715 3.68489 9.37805 3 10.3663 3H13.6337C14.6219 3 15.4728 3.68489 15.6667 4.63636C15.7959 5.27068 16.3631 5.76086 17.022 5.76086C18.4027 5.82123 19.3044 5.99013 20.0248 6.46268C20.51 6.78104 20.9267 7.19014 21.251 7.6666C22 8.76721 22 10.2994 22 13.3636C22 16.4279 22 17.9601 21.251 19.0607C20.9267 19.5371 20.51 19.9462 20.0248 20.2646C18.9038 21 17.3433 21 14.2222 21H9.77778C6.65675 21 5.09624 21 3.97524 20.2646C3.48995 19.9462 3.07328 19.5371 2.74902 19.0607C2.53746 18.7498 2.38566 18.4045 2.27673 18"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Miter
                )
            }.build()
            return _PersonAdd!!
        }

    private var _QueueMusic: ImageVector? = null
    val QueueMusic: ImageVector
        get() {
            if (_QueueMusic != null) {
                return _QueueMusic!!
            }
            _QueueMusic = Builder(
                name = "QueueMusic",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f
            ).apply {

                addPath(
                    pathData = addPathNodes("M12.5606 12.6995L12.2076 13.3612L12.2076 13.3612L12.5606 12.6995ZM13.4429 13.17L13.7958 12.5083L13.7958 12.5083L13.4429 13.17ZM14.4394 11.3015L14.7924 10.6398L14.7924 10.6398L14.4394 11.3015ZM13.5571 10.8309L13.91 10.1692L13.91 10.1692L13.5571 10.8309ZM12.0018 11.6066L12.7505 11.652L12.7505 11.652L12.0018 11.6066ZM13.4163 10.7579L13.1041 11.4398L13.1041 11.4398L13.4163 10.7579ZM14.9995 12.1687L15.7491 12.1449L15.7491 12.1449L14.9995 12.1687ZM14.4984 11.3335L14.8722 10.6833L14.8722 10.6833L14.4984 11.3335ZM13.5837 13.2431L13.8959 12.5611L13.8959 12.5611L13.5837 13.2431ZM14.9982 12.3944L14.2495 12.349L14.2495 12.349L14.9982 12.3944ZM12.5016 12.6674L12.1278 13.3177L12.1278 13.3177L12.5016 12.6674ZM12.0005 11.8323L11.2509 11.8561L11.2509 11.8561L12.0005 11.8323ZM11.25 16.5005C11.25 16.9147 10.9142 17.2505 10.5 17.2505V18.7505C11.7426 18.7505 12.75 17.7431 12.75 16.5005H11.25ZM10.5 17.2505C10.0858 17.2505 9.75 16.9147 9.75 16.5005H8.25C8.25 17.7431 9.25736 18.7505 10.5 18.7505V17.2505ZM9.75 16.5005C9.75 16.0863 10.0858 15.7505 10.5 15.7505V14.2505C9.25736 14.2505 8.25 15.2578 8.25 16.5005H9.75ZM10.5 15.7505C10.9142 15.7505 11.25 16.0863 11.25 16.5005H12.75C12.75 15.2578 11.7426 14.2505 10.5 14.2505V15.7505ZM12.75 16.5005V12.0005H11.25V16.5005H12.75ZM12.2076 13.3612L13.09 13.8318L13.7958 12.5083L12.9135 12.0377L12.2076 13.3612ZM14.7924 10.6398L13.91 10.1692L13.2042 11.4927L14.0865 11.9633L14.7924 10.6398ZM12.75 11.7652C12.75 11.721 12.75 11.6929 12.7502 11.672C12.7504 11.6505 12.7507 11.6476 12.7505 11.652L11.2532 11.5612C11.2496 11.6203 11.25 11.6931 11.25 11.7652H12.75ZM13.91 10.1692C13.8464 10.1352 13.7824 10.1007 13.7285 10.076L13.1041 11.4398C13.1 11.438 13.1027 11.4391 13.1218 11.449C13.1404 11.4587 13.1652 11.4719 13.2042 11.4927L13.91 10.1692ZM12.7505 11.652C12.7611 11.4767 12.9444 11.3667 13.1041 11.4398L13.7285 10.076C12.6108 9.56427 11.3277 10.3341 11.2532 11.5612L12.7505 11.652ZM15.75 12.2358C15.75 12.2067 15.7501 12.1755 15.7491 12.1449L14.2499 12.1925C14.2498 12.1917 14.2499 12.1929 14.25 12.1999C14.25 12.2077 14.25 12.2179 14.25 12.2358H15.75ZM14.0865 11.9633C14.1023 11.9717 14.1113 11.9765 14.1181 11.9802C14.1243 11.9835 14.1253 11.9842 14.1246 11.9838L14.8722 10.6833C14.8456 10.668 14.8181 10.6535 14.7924 10.6398L14.0865 11.9633ZM15.7491 12.1449C15.7298 11.5386 15.398 10.9856 14.8722 10.6833L14.1246 11.9838C14.1997 12.0269 14.2471 12.1059 14.2499 12.1925L15.7491 12.1449ZM13.09 13.8318C13.1536 13.8657 13.2176 13.9003 13.2715 13.925L13.8959 12.5611C13.9 12.563 13.8973 12.5619 13.8782 12.552C13.8596 12.5423 13.8348 12.5291 13.7958 12.5083L13.09 13.8318ZM14.25 12.2358C14.25 12.28 14.25 12.308 14.2498 12.329C14.2496 12.3505 14.2493 12.3534 14.2495 12.349L15.7468 12.4398C15.7504 12.3807 15.75 12.3079 15.75 12.2358H14.25ZM13.2715 13.925C14.3892 14.4367 15.6723 13.6668 15.7468 12.4398L14.2495 12.349C14.2389 12.5243 14.0556 12.6342 13.8959 12.5611L13.2715 13.925ZM12.9135 12.0377C12.8977 12.0293 12.8887 12.0245 12.8819 12.0208C12.8757 12.0174 12.8746 12.0168 12.8754 12.0172L12.1278 13.3177C12.1544 13.3329 12.1819 13.3475 12.2076 13.3612L12.9135 12.0377ZM11.25 11.7652C11.25 11.7943 11.2499 11.8255 11.2509 11.8561L12.7501 11.8084C12.7502 11.8093 12.7501 11.808 12.75 11.8011C12.75 11.7933 12.75 11.7831 12.75 11.7652H11.25ZM12.8754 12.0172C12.8003 11.974 12.7529 11.895 12.7501 11.8084L11.2509 11.8561C11.2702 12.4623 11.602 13.0154 12.1278 13.3177L12.8754 12.0172Z"),
                    fill = SolidColor(Color.White),
                    stroke = null,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M19.5617 7C19.7904 5.69523 18.7863 4.5 17.4617 4.5H6.53788C5.21323 4.5 4.20922 5.69523 4.43784 7"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M17.4999 4.5C17.5283 4.24092 17.5425 4.11135 17.5427 4.00435C17.545 2.98072 16.7739 2.12064 15.7561 2.01142C15.6497 2 15.5194 2 15.2588 2H8.74099C8.48035 2 8.35002 2 8.24362 2.01142C7.22584 2.12064 6.45481 2.98072 6.45704 4.00434C6.45727 4.11135 6.47146 4.2409 6.49983 4.5"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M21.1935 16.793C20.8437 19.2739 20.6689 20.5143 19.7717 21.2572C18.8745 22 17.5512 22 14.9046 22H9.09536C6.44881 22 5.12553 22 4.22834 21.2572C3.33115 20.5143 3.15626 19.2739 2.80648 16.793L2.38351 13.793C1.93748 10.6294 1.71447 9.04765 2.66232 8.02383C3.61017 7 5.29758 7 8.67239 7H15.3276C18.7024 7 20.3898 7 21.3377 8.02383C22.0865 8.83268 22.1045 9.98979 21.8592 12"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Miter
                )
            }.build()
            return _QueueMusic!!
        }

}
