package(default_visibility = ["//visibility:public"])

java_library(
  name = "signlib",
  srcs = glob(["src/**/*.java", "src/*.java"]),
  deps = [
    "@duckutil//:duckutil_lib",
    "@duckutil//:sql_lib",
    "@maven//:commons_pool_commons_pool",
    "@maven//:commons_dbcp_commons_dbcp",
    "@maven//:com_amazonaws_aws_java_sdk",
    "@maven//:net_minidev_json_smart",
    "@maven//:org_elasticsearch_client_elasticsearch_rest_client",
    "@maven//:org_elasticsearch_client_elasticsearch_rest_high_level_client",
    "@maven//:org_apache_httpcomponents_httpcore",
    "@maven//:org_elasticsearch_elasticsearch",
    "@maven//:com_google_guava_guava",
    "@maven//:com_amazonaws_aws_java_sdk_sns",
    "@maven//:com_amazonaws_aws_java_sdk_core",
    "@maven//:org_eclipse_paho_org_eclipse_paho_client_mqttv3",
  ],
)

java_binary(
  name = "SignUpdate",
  main_class = "duckutil.sign.SignUpdate",
  runtime_deps = [
    ":signlib",
  ],
)
java_binary(
  name = "Display",
  main_class = "duckutil.sign.Display",
  runtime_deps = [
    ":signlib",
  ],
)
java_binary(
  name = "SignImage",
  main_class = "duckutil.sign.SignImage",
  runtime_deps = [
    ":signlib",
  ],
)



java_binary(
  name = "FontSelect",
  main_class = "duckutil.sign.FontSelect",
  runtime_deps = [
    ":signlib",
  ],
)

