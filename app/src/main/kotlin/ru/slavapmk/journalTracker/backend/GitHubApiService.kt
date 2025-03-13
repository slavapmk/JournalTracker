package ru.slavapmk.journalTracker.backend

import retrofit2.http.GET
import retrofit2.http.Path
import ru.slavapmk.journalTracker.backendModels.github.GithubReleases

interface GitHubApiService {
    @GET("repos/{owner}/{repo}/releases")
    suspend fun getReleases(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): GithubReleases
}