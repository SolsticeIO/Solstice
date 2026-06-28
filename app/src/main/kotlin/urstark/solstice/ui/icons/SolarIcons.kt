/*
 * Solstice (2026)
 * © Stark — github.com/urstark
 * 
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */
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
                    pathData = addPathNodes("M6.28571 19C3.91878 19 2 17.1038 2 14.7647C2 12.4256 3.91878 10.5294 6.28571 10.5294C6.56983 10.5294 6.8475 10.5567 7.11616 10.6089M14.381 8.02721C14.9767 7.81911 15.6178 7.70588 16.2857 7.70588C16.9404 7.70588 17.5693 7.81468 18.1551 8.01498M7.11616 10.6089C6.88706 9.9978 6.7619 9.33687 6.7619 8.64706C6.7619 5.52827 9.32028 3 12.4762 3C15.4159 3 17.8371 5.19371 18.1551 8.01498M7.11616 10.6089C7.68059 10.7184 8.20528 10.9374 8.66667 11.2426M18.1551 8.01498C20.393 8.78024 22 10.8811 22 13.3529C22 16.0599 20.0726 18.3221 17.5 18.8722"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M10 19.8L11.1429 21L14 18"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
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
                    pathData = addPathNodes("M6.28571 19C3.91878 19 2 17.1038 2 14.7647C2 12.4256 3.91878 10.5294 6.28571 10.5294C6.56983 10.5294 6.8475 10.5567 7.11616 10.6089M14.381 8.02721C14.9767 7.81911 15.6178 7.70588 16.2857 7.70588C16.9404 7.70588 17.5693 7.81468 18.1551 8.01498M7.11616 10.6089C6.88706 9.9978 6.7619 9.33687 6.7619 8.64706C6.7619 5.52827 9.32028 3 12.4762 3C15.4159 3 17.8371 5.19371 18.1551 8.01498M7.11616 10.6089C7.68059 10.7184 8.20528 10.9374 8.66667 11.2426M18.1551 8.01498C20.393 8.78024 22 10.8811 22 13.3529C22 16.0599 20.0726 18.3221 17.5 18.8722"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M14 19H12L10 19"),
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
                    pathData = addPathNodes("M2.38351 13.793C1.93748 10.6294 1.71447 9.04765 2.66232 8.02383C3.61017 7 5.29758 7 8.67239 7H15.3276C18.7024 7 20.3898 7 21.3377 8.02383C22.2855 9.04765 22.0625 10.6294 21.6165 13.793L21.1935 16.793C20.8437 19.2739 20.6689 20.5143 19.7717 21.2572C18.8745 22 17.5512 22 14.9046 22H9.09536C6.44881 22 5.12553 22 4.22834 21.2572C3.33115 20.5143 3.15626 19.2739 2.80648 16.793L2.38351 13.793Z"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M19.5619 7C19.7905 5.69523 18.7865 4.5 17.4619 4.5H6.53806C5.21341 4.5 4.2094 5.69523 4.43803 7"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M17.5 4.5C17.5284 4.24092 17.5426 4.11135 17.5428 4.00435C17.545 2.98072 16.774 2.12064 15.7562 2.01142C15.6498 2 15.5195 2 15.2588 2H8.74105C8.48041 2 8.35008 2 8.24368 2.01142C7.2259 2.12064 6.45487 2.98072 6.4571 4.00434C6.45733 4.11135 6.47152 4.2409 6.49989 4.5"),
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
                    pathData = addPathNodes("M20 20L17.1158 17.8514C16.1857 17.1586 14.8006 17.0896 13.7768 17.6851L13.51 17.8403C12.7985 18.2542 11.8306 18.1848 11.2157 17.6758L7.3775 14.4989C6.61142 13.8648 5.38257 13.8309 4.56722 14.4214L3.24329 15.3803"),
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
                    pathData = addPathNodes("M21 17V12C21 7.02944 16.9706 3 12 3C7.02944 3 3 7.02944 3 12V17"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M22 15.5V17.5"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M2 15.5V17.5"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M8 13.8446C8 13.0802 8 12.698 7.82526 12.4323C7.73733 12.2985 7.62061 12.188 7.4844 12.1095C7.21371 11.9535 6.84812 11.9896 6.11694 12.0617C4.88487 12.1831 4.26884 12.2439 3.82737 12.5764C3.60394 12.7448 3.41638 12.9593 3.27646 13.2067C3 13.6955 3 14.3395 3 15.6276V17.1933C3 18.4685 3 19.1061 3.28198 19.5986C3.38752 19.7829 3.51981 19.9491 3.67416 20.0913C4.08652 20.4714 4.68844 20.5901 5.89227 20.8275C6.73944 20.9945 7.16302 21.078 7.47564 20.9021C7.591 20.8372 7.69296 20.7493 7.77572 20.6434C8 20.3565 8 19.9078 8 19.0104V13.8446Z"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M16 13.8446C16 13.0802 16 12.698 16.1747 12.4323C16.2627 12.2985 16.3794 12.188 16.5156 12.1095C16.7863 11.9535 17.1519 11.9896 17.8831 12.0617C19.1151 12.1831 19.7312 12.2439 20.1726 12.5764C20.3961 12.7448 20.5836 12.9593 20.7235 13.2067C21 13.6955 21 14.3395 21 15.6276V17.1933C21 18.4685 21 19.1061 20.718 19.5986C20.6125 19.7829 20.4802 19.9491 20.3258 20.0913C19.9135 20.4714 19.3116 20.5901 18.1077 20.8275C17.2606 20.9945 16.837 21.078 16.5244 20.9021C16.409 20.8372 16.307 20.7493 16.2243 20.6434C16 20.3565 16 19.9078 16 19.0104V13.8446Z"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
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
                    pathData = addPathNodes("M19.562 7C19.7906 5.69523 18.7866 4.5 17.4619 4.5H6.53812C5.21347 4.5 4.20946 5.69523 4.43809 7"),
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
                    pathData = addPathNodes("M12.5606 12.6995L12.2076 13.3612L12.2076 13.3612L12.5606 12.6995ZM13.4429 13.17L13.7958 12.5083L13.7958 12.5083L13.4429 13.17ZM14.4394 11.3015L14.7924 10.6398L14.7924 10.6398L14.4394 11.3015ZM13.5571 10.8309L13.91 10.1692L13.91 10.1692L13.5571 10.8309ZM12.0018 11.6066L12.7505 11.652L12.7505 11.652L12.0018 11.6066ZM13.4163 10.7579L13.1041 11.4398L13.1041 11.4398L13.4163 10.7579ZM14.9995 12.1687L15.7491 12.1449L15.7491 12.1449L14.9995 12.1687ZM14.4984 11.3335L14.8722 10.6833L14.8722 10.6833L14.4984 11.3335ZM13.5837 13.2431L13.8959 12.5611L13.8959 12.5611L13.5837 13.2431ZM14.9982 12.3944L14.2495 12.349L14.2495 12.349L14.9982 12.3944ZM12.5016 12.6674L12.1278 13.3177L12.1278 13.3177L12.5016 12.6674ZM12.0005 11.8323L11.2509 11.8561L11.2509 11.8561L12.0005 11.8323ZM11.25 16.5005C11.25 16.9147 10.9142 17.2505 10.5 17.2505V18.7505C11.7426 18.7505 12.75 17.7431 12.75 16.5005H11.25ZM10.5 17.2505C10.0858 17.2505 9.75 16.9147 9.75 16.5005H8.25C8.25 17.7431 9.25736 18.7505 10.5 18.7505V17.2505ZM9.75 16.5005C9.75 16.0863 10.0858 15.7505 10.5 15.7505V14.2505C9.25736 14.2505 8.25 15.2578 8.25 16.5005H9.75ZM10.5 15.7505C10.9142 15.7505 11.25 16.0863 11.25 16.5005H12.75C12.75 15.2578 11.7426 14.2505 10.5 14.2505V15.7505ZM12.75 16.5005V12.0005H11.25V16.5005H12.75ZM12.2076 13.3612L13.09 13.8318L13.7958 12.5083L12.9135 12.0377L12.2076 13.3612ZM14.7924 10.6398L13.91 10.1692L13.2042 11.4927L14.0865 11.9633L14.7924 10.6398ZM12.75 11.7652C12.75 11.721 12.75 11.6929 12.7502 11.672C12.7504 11.6505 12.7507 11.6476 12.7505 11.652L11.2532 11.5612C11.2496 11.6203 11.25 11.6931 11.25 11.7652H12.75ZM13.91 10.1692C13.8464 10.1352 13.7824 10.1007 13.7285 10.076L13.1041 11.4398C13.1 11.438 13.1027 11.4391 13.1218 11.449C13.1404 11.4587 13.1652 11.4719 13.2042 11.4927L13.91 10.1692ZM12.7505 11.652C12.7611 11.4767 12.9444 11.3667 13.1041 11.4398L13.7285 10.076C12.6108 9.56427 11.3277 10.3341 11.2532 11.5612L12.7505 11.652ZM15.75 12.2358C15.75 12.2067 15.7501 12.1755 15.7491 12.1449L14.2499 12.1925C14.2498 12.1917 14.2499 12.1929 14.25 12.1999C14.25 12.2077 14.25 12.2179 14.25 12.2358H15.75ZM14.0865 11.9633C14.1023 11.9717 14.1113 11.9765 14.1181 11.9802C14.1243 11.9835 14.1253 11.9842 14.1246 11.9838L14.8722 10.6833C14.8456 10.668 14.8181 10.6535 14.7924 10.6398L14.0865 11.9633ZM15.7491 12.1449C15.7298 11.5386 15.398 10.9856 14.8722 10.6833L14.1246 11.9838C14.1997 12.0269 14.2471 12.1059 14.2499 12.1925L15.7491 12.1449ZM13.09 13.8318C13.1536 13.8657 13.2176 13.9003 13.2715 13.925L13.8959 12.5611C13.9 12.563 13.8973 12.5619 13.8782 12.552C13.8596 12.5423 13.8348 12.5291 13.7958 12.5083L13.09 13.8318ZM14.25 12.2358C14.25 12.28 14.25 12.308 14.2498 12.329C14.2496 12.3505 14.2493 12.3534 14.2495 12.349L15.7468 12.4398C15.7504 12.3807 15.75 12.3079 15.75 12.2358H14.25ZM13.2715 13.925C14.3892 14.4367 15.6723 13.6668 15.7468 12.4398L14.2495 12.349C14.2389 12.5243 14.0556 12.6342 13.8959 12.5611L13.2715 13.925ZM12.9135 12.0377C12.8977 12.0293 12.8887 12.0245 12.8819 12.0208C12.8757 12.0174 12.8746 12.0168 12.8754 12.0172L12.1278 13.3177C12.1544 13.3329 12.1819 13.3475 12.2076 13.3612L12.9135 12.0377ZM11.25 11.7652C11.25 11.7943 11.2499 11.8255 11.2509 11.8561L12.7501 11.8084C12.7502 11.8093 12.7501 11.808 12.75 11.8011C12.75 11.7933 12.75 11.7831 12.75 11.7652H11.25ZM12.8754 12.0172C12.8003 11.974 12.7529 11.895 12.7501 11.8084L11.2509 11.8561C11.2702 12.4623 11.602 13.0154 12.1278 13.3177L12.8754 12.0172Z"),
                    fill = SolidColor(Color.White),
                    stroke = null,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M2.38351 13.793C1.93748 10.6294 1.71447 9.04765 2.66232 8.02383C3.61017 7 5.29758 7 8.67239 7H15.3276C18.7024 7 20.3898 7 21.3377 8.02383C22.2855 9.04765 22.0625 10.6294 21.6165 13.793L21.1935 16.793C20.8437 19.2739 20.6689 20.5143 19.7717 21.2572C18.8745 22 17.5512 22 14.9046 22H9.09536C6.44881 22 5.12553 22 4.22834 21.2572C3.33115 20.5143 3.15626 19.2739 2.80648 16.793L2.38351 13.793Z"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Butt,
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
                    pathData = addPathNodes("M21 17V6"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
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
                    pathData = addPathNodes("M 12.0 2.0 A 4.0 4.0 0 1 0 12.0 10.0 A 4.0 4.0 0 1 0 12.0 2.0 Z"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M15 13.3271C14.0736 13.1162 13.0609 13 12 13C7.58172 13 4 15.0147 4 17.5C4 19.9853 4 22 12 22C17.6874 22 19.3315 20.9817 19.8068 19.5"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M 18.0 12.0 A 4.0 4.0 0 1 0 18.0 20.0 A 4.0 4.0 0 1 0 18.0 12.0 Z"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M18 14.6665V17.3332"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                )
                addPath(
                    pathData = addPathNodes("M16.6665 16L19.3332 16"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
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
                    pathData = addPathNodes("M21 6L3 6"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M21 10L3 10"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M11 14L3 14"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M11 18H3"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Miter
                )
                addPath(
                    pathData = addPathNodes("M18.875 14.1185C20.5288 15.0733 21.3558 15.5507 21.4772 16.2396C21.5076 16.4119 21.5076 16.5882 21.4772 16.7605C21.3558 17.4493 20.5288 17.9268 18.875 18.8816C17.2212 19.8365 16.3942 20.3139 15.737 20.0746C15.5725 20.0148 15.4199 19.9266 15.2858 19.8142C14.75 19.3646 14.75 18.4097 14.75 16.5C14.75 14.5904 14.75 13.6355 15.2858 13.1859C15.4199 13.0734 15.5725 12.9853 15.737 12.9254C16.3942 12.6862 17.2212 13.1636 18.875 14.1185Z"),
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter
                )
            }.build()
            return _QueueMusic!!
        }

}
