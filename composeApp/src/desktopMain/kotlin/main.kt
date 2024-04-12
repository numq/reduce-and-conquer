import androidx.compose.ui.window.singleWindowApplication
import application.Application
import di.appModule
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.startKoin
import reduce_and_conquer.composeapp.generated.resources.Res
import reduce_and_conquer.composeapp.generated.resources.application_name
import java.awt.Dimension

@OptIn(ExperimentalResourceApi::class)
fun main() = singleWindowApplication {
    val windowSize = Dimension(700, 700)

    window.title = stringResource(Res.string.application_name)
    window.minimumSize = windowSize
    window.size = windowSize

    startKoin { modules(appModule) }

    Application()
}