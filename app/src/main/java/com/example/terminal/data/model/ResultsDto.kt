package com.example.bookshelf

import com.google.gson.annotations.SerializedName


data class ResultsDto (
  @SerializedName("o"  ) var o  : Float? = null,
  @SerializedName("c"  ) var c  : Float? = null,
  @SerializedName("h"  ) var h  : Float? = null,
  @SerializedName("l"  ) var l  : Float? = null,
  @SerializedName("t"  ) var t  : Long?    = null,
)