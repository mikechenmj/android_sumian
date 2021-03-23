package com.sumian.devicedemo.dfuDemo

const val DFU_FILE_URL = "https://sumian-test.oss-cn-shenzhen.aliyuncs.com/firmware/0/mon_dfu_upgrade_sec_v1.0.9_20200509.zip"

const val CHANNEL_SUCCESS = 1
const val CHANNEL_FAILED = -1
const val RETRY_MAX = 3
const val COMPUTE_RESULT_START = 0
const val COMPUTE_RESULT_COMPLETE = 1
const val COMPUTE_RESULT_CONTINUE = 2

const val STEP = 20
const val INIT_PRN = 0
const val IMAGE_PRN = 5