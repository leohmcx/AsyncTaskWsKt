package br.edu.ifsp.scl.asynctaskwskt

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import br.edu.ifsp.scl.asynctaskwskt.MainActivity.constantes.URL_BASE
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.IOException
import java.lang.Thread.sleep
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    object constantes { val URL_BASE = "http://www.nobile.pro.br/sdm/" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buscarInformacoesBt.setOnClickListener {                            // Seta o Listener para o botão usando uma função lambda
            val buscarTextoAt = BuscarTextoAt()                             // Disparando AsyncTask para buscar texto
            val buscarDataAt = BuscarDataAt()
            buscarTextoAt.execute(URL_BASE + "texto.php")
            buscarDataAt.execute(URL_BASE + "data.php")
        }
    }
    private inner class BuscarTextoAt : AsyncTask<String, Int, String>() {  // AsyncTask que fará acesso ao WebService
        override fun onPreExecute() {                                       // Executa na mesma Thread de UI
            super.onPreExecute()
            toast("Buscando String no Web Service")
            progressBar.visibility = View.VISIBLE                                               // Mostrando a barra de progresso
        }
        override fun doInBackground(vararg params: String?): String {                           // Executa numa outra Thread em background
            val url = params[0]                                                                 // Pegando URL na primeira posição do params
            val stringBufferResposta: StringBuffer = StringBuffer()                             // Criando um StringBuffer para receber a resposta do Web Service
            try {
                val conexao = URL(url).openConnection() as HttpURLConnection                    // Criando uma conexão HTTP a partir da URL
                    if (conexao.responseCode == HttpURLConnection.HTTP_OK) {
                        val inputStream = conexao.inputStream                                   // Caso a conexão seja bem sucedida, resgata o InputStream da mesma
                        val bufferedReader = BufferedInputStream(inputStream).bufferedReader()  // Cria um BufferedReader a partir do InputStream
                        val respostaList = bufferedReader.readLines()                           // Lê o bufferedReader para uma lista de Strings
                        respostaList.forEach { stringBufferResposta.append(it) }                // "Appenda" cada String da lista ao StringBuffer
                    }
            } catch (ioe: IOException) {
                toast("Erro na conexão!")
            }

            for (i in 1..10) {                                                                  // Simulando notificação do progresso para Thread de UI
                publishProgress(i) /* Envia um inteiro para ser publicado como progresso.Esse valor é recebido pela função  callback onProgressUpdate*/
                sleep(500)                                                                // Dormindo por 0.5 s para simular o atraso de rede
            }
            return stringBufferResposta.toString()                                              // Retorna a String formada a partir do StringBuffer
        }
        override fun onPostExecute(result: String?) {                                           // Executa na mesma Thread de UI
            super.onPostExecute(result)
            toast("Texto recuperado com sucesso")
            textoTv.text = result                                                               // Altera o TextView com o texto recuperado
            progressBar.visibility = View.GONE                                                  // Tornando a barra de progresso invisível
        }
        override fun onProgressUpdate(vararg values: Int?) { // Executa na Thread de UI, é chamado sempre após uma publishProgress
            values[0]?.apply { progressBar.progress = this } // Se o valor de progresso não for nulo, atualiza a barra de progresso
        }
    }



    private inner class BuscarDataAt : AsyncTask<String, Int, JSONObject>() {  // AsyncTask que fará acesso ao WebService
        override fun onPreExecute() {                                       // Executa na mesma Thread de UI
            super.onPreExecute()
            toast("Buscando Data no Web Service")
            progressBar.visibility = View.VISIBLE                                               // Mostrando a barra de progresso
        }

        override fun doInBackground(vararg params: String?): JSONObject {                       // Executa numa outra Thread em background
            val url = params[0]                                                                 // Pegando URL na primeira posição do params
            val stringBufferResposta: StringBuffer = StringBuffer()                             // Criando um StringBuffer para receber a resposta do Web Service
            try {
                val conexao = URL(url).openConnection() as HttpURLConnection                    // Criando uma conexão HTTP a partir da URL
                if (conexao.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = conexao.inputStream                                   // Caso a conexão seja bem sucedida, resgata o InputStream da mesma
                    val bufferedReader = BufferedInputStream(inputStream).bufferedReader()  // Cria um BufferedReader a partir do InputStream
                    val respostaList = bufferedReader.readLines()                           // Lê o bufferedReader para uma lista de Strings
                    respostaList.forEach { stringBufferResposta.append(it) }                // "Appenda" cada String da lista ao StringBuffer
                }
                return JSONObject(stringBufferResposta.toString())
            } catch (ioe: IOException) {
                toast("Erro na conexão!")
            } catch (jsone: JSONException) {
                jsone.printStackTrace()
            }

            for (i in 1..10) {                                                                  // Simulando notificação do progresso para Thread de UI
                publishProgress(i) /* Envia um inteiro para ser publicado como progresso.Esse valor é recebido pela função  callback onProgressUpdate*/
                sleep(500)                                                                // Dormindo por 0.5 s para simular o atraso de rede
            }
            return JSONObject()                                                                   // Retorna a String formada a partir do StringBuffer
        }

        override fun onPostExecute(result: JSONObject?) {                                           // Executa na mesma Thread de UI
            var data: String? = ""
            var hora: String? = ""
            var ds: String? = ""
            super.onPostExecute(result)
            try {
                data = "${result?.getInt("mday")}/${result?.getInt("mon")}/${result?.getInt("year")}"
                hora = "${result?.getInt("hours")}:${result?.getInt("minutes")}:${result?.getInt("seconds")}"
                ds = result?.getString("weekday");
            }
            catch (jsone: JSONException) {
                jsone.printStackTrace();
            }
            tv_data.setText("$data\n$hora\n$ds");                                                 // Altera o TextView com o texto recuperado
            progressBar.visibility = View.GONE                                                  // Tornando a barra de progresso invisível
        }
        override fun onProgressUpdate(vararg values: Int?) { // Executa na Thread de UI, é chamado sempre após uma publishProgress
            values[0]?.apply { progressBar.progress = this } // Se o valor de progresso não for nulo, atualiza a barra de progresso
        }
    }
}