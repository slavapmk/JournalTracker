package ru.slavapmk.journalTracker.backendModels.github


import com.google.gson.annotations.SerializedName

data class GithubAsset(
    @SerializedName("browser_download_url")
    val browserDownloadUrl: String,
    @SerializedName("content_type")
    val contentType: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("download_count")
    val downloadCount: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("label")
    val label: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("node_id")
    val nodeId: String,
    @SerializedName("size")
    val size: Int,
    @SerializedName("state")
    val state: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("uploader")
    val uploader: GithubUploader,
    @SerializedName("url")
    val url: String
)