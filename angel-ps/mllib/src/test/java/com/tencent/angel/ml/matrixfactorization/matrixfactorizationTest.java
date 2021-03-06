/*
 * Tencent is pleased to support the open source community by making Angel available.
 *
 * Copyright (C) 2017 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package com.tencent.angel.ml.matrixfactorization;

import com.tencent.angel.conf.AngelConfiguration;
import com.tencent.angel.ml.conf.MLConf;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.mapreduce.lib.input.CombineTextInputFormat;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)

public class matrixfactorizationTest {

  private Configuration conf = new Configuration();

  static {
    PropertyConfigurator.configure("../conf/log4j.properties");
  }


  @Before
  public void setup() throws Exception {
    String inputPath = "./src/test/data/recommendation/MovieLensDataSet";

    // Set local deploy mode
    conf.set(AngelConfiguration.ANGEL_DEPLOY_MODE, "LOCAL");
    // Set basic configuration keys
    conf.setBoolean("mapred.mapper.new-api", true);
    conf.set(AngelConfiguration.ANGEL_INPUTFORMAT_CLASS, CombineTextInputFormat.class.getName());
    conf.setBoolean(AngelConfiguration.ANGEL_JOB_OUTPUT_PATH_DELETEONEXIST, true);

    //set angel resource parameters #worker, #task, #PS
    conf.setInt(AngelConfiguration.ANGEL_WORKERGROUP_NUMBER, 1);
    conf.setInt(AngelConfiguration.ANGEL_WORKER_TASK_NUMBER, 1);
    conf.setInt(AngelConfiguration.ANGEL_PS_NUMBER, 1);

    String LOCAL_FS = LocalFileSystem.DEFAULT_FS;
    String TMP_PATH = System.getProperty("java.io.tmpdir", "/tmp");

    // Set trainning data, save model, log path
    conf.set(AngelConfiguration.ANGEL_TRAIN_DATA_PATH, inputPath);
    conf.set(AngelConfiguration.ANGEL_SAVE_MODEL_PATH, LOCAL_FS + TMP_PATH + "/model");
    conf.set(AngelConfiguration.ANGEL_LOG_PATH, LOCAL_FS + TMP_PATH + "/Log/log");
    conf.set(MLConf.ML_MF_USER_OUTPUT_PATH(), LOCAL_FS + TMP_PATH + "/usermodel");
    // Set actionType train
    conf.set(AngelConfiguration.ANGEL_ACTION_TYPE, MLConf.ANGEL_ML_TRAIN());

    // Set MF algorithm parameters
    conf.set(MLConf.ML_MF_RANK(), "200");
    conf.set(MLConf.ML_EPOCH_NUM(), "20");
    conf.set(MLConf.ML_MF_ROW_BATCH_NUM(), "2");
    conf.set(MLConf.ML_MF_ITEM_NUM(), "1683");
    conf.set(MLConf.ML_MF_LAMBDA(), "0.01");
    conf.set(MLConf.ML_MF_ETA(), "0.0005");
  }


  @Test
  public void runTest() throws Exception {
    MatrixFactorizationRunner runner = new MatrixFactorizationRunner();
    runner.train(conf);
  }

}
