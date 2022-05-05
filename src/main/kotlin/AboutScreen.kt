import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import ui.Colors
import ui.TypographyAbout
import util.openWebpage
import java.net.URL

@Composable
fun AboutScreen(
    onCloseRequest: () -> Unit
) = Window(
    title = "About",
    onCloseRequest = onCloseRequest,
    state = rememberWindowState(
        size = DpSize(400.dp, 300.dp)
    ),
    alwaysOnTop = true,
    resizable = false
) {
    MaterialTheme(
        colors = Colors,
        typography = TypographyAbout
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Process Scheduling Simulator",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h6
            )

            Box(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = "Team information",
                style = MaterialTheme.typography.subtitle1
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.caption,
                text = "Operating system (1) 1st TEAM, Koreatech"
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.overline,
                text = "Awhi Lee  Heekwon Kang\n" +
                        "Jaeyong Kim  Seungmin Yang"
            )
            Row {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.caption,
                    text = "Instructor: "
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.overline,
                    text = "Duksu Kim"
                )
            }


            Column(
                modifier = Modifier.padding(top = 16.dp).clickable {
                    openWebpage(URL("https://github.com/Yang-Seungmin/os-process-scheduling-simulator"))
                },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Image(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource("github_mark.png"),
                    contentDescription = "",
                    contentScale = ContentScale.FillHeight
                )
                Text(
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.caption,
                    text = "Github Repository | Open Source Libraries"
                )
            }
        }
    }
}