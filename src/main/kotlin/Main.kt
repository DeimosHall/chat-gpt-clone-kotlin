import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.*
import java.lang.Exception

suspend fun main() {
    var prompt = "The following is a conversation with an AI assistant. The assistant is helpful, creative, clever, and very friendly."
    println("Type exit to finish chatting")

    while (true) {
        print("You: ")
        val question: String = readLine()!!
        prompt += "\n\nHuman: $question \nAI:"

        if (question == "exit") {
            System.exit(0)
        }

        makeCompletionRequest(prompt)
    }
}

suspend fun makeCompletionRequest(prompt: String) {
    val API_KEY = "Bearer $MY_API_KEY"
    val contentType = "application/json"

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val openAiService = retrofit.create(OpenAiService::class.java)

    val request = CompletionRequest(
        model = "text-davinci-003",
        prompt = prompt,
        temperature = 0.9,
        max_tokens = 150,
        top_p = 1,
        frequency_penalty = 0.0,
        presence_penalty = 0.6,
        stop = listOf(" Human:", " AI:")
    )

    withContext(Dispatchers.IO) {
        try {
            val response = openAiService.completions(contentType, API_KEY, request)

            if (response.isSuccessful) {
                println("AI: ${response.body()?.choices?.first()?.text}")
            } else {
                println("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            println("Error: $e")
        }
    }
}