// Copyright (c) 2019 PaddlePaddle Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#pragma once

#include "Utils.h"
#include "paddle_api.h"
#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <opencv2/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgcodecs.hpp>
#include <opencv2/imgproc.hpp>
#include <string>
#include <vector>
#include <jni.h>
#include "Native.h"
#include "firebase/storage.h"
#include "firebase/app.h"
#include "firebase/auth.h"
#include "firebase/util.h"
#include "firebase/log.h"
#include "main.h"

struct Face {
    // Detection result: face rectangle
    cv::Rect roi;
    // Classification result: confidence
    float confidence;
    // Classification result : class id
    int classid;
};

class FaceDetector {
public:
    explicit FaceDetector(const std::string &modelDir, const int cpuThreadNum,
                          const std::string &cpuPowerMode, float inputScale,
                          const std::vector<float> &inputMean,
                          const std::vector<float> &inputStd,
                          float scoreThreshold);

    void Predict(const cv::Mat &rgbaImage, std::vector<Face> *faces,
                 double *preprocessTime, double *predictTime,
                 double *postprocessTime);

private:
    void Preprocess(const cv::Mat &rgbaImage);

    void Postprocess(const cv::Mat &rgbaImage, std::vector<Face> *faces);

private:
    float inputScale_;
    std::vector<float> inputMean_;
    std::vector<float> inputStd_;
    float scoreThreshold_;
    std::shared_ptr<paddle::lite_api::PaddlePredictor> predictor_;
};

class MaskClassifier {
public:
    explicit MaskClassifier(const std::string &modelDir, const int cpuThreadNum,
                            const std::string &cpuPowerMode, int inputWidth,
                            int inputHeight, const std::vector<float> &inputMean,
                            const std::vector<float> &inputStd);

    void Predict(const cv::Mat &rgbImage, std::vector<Face> *faces,
                 double *preprocessTime, double *predictTime,
                 double *postprocessTime);

private:
    void Preprocess(const cv::Mat &rgbaImage, const std::vector<Face> &faces);

    void Postprocess(std::vector<Face> *faces);

private:
    int inputWidth_;
    int inputHeight_;
    std::vector<float> inputMean_;
    std::vector<float> inputStd_;
    std::shared_ptr<paddle::lite_api::PaddlePredictor> predictor_;
};

class Pipeline {
public:
    Pipeline();

    Pipeline(const std::string &fdtModelDir, const int fdtCPUThreadNum,
             const std::string &detCPUPowerMode, float fdtInputScale,
             const std::vector<float> &fdtInputMean,
             const std::vector<float> &fdtInputStd, float fdtScoreThreshold,
             const std::string &mclModelDir, const int mclCPUThreadNum,
             const std::string &mclCPUPowerMode, int mclInputWidth,
             int mclInputHeight, const std::vector<float> &mclInputMean,
             const std::vector<float> &mclInputStd);

    bool Process(int inTextureId, int outTextureId, int textureWidth,
                 int textureHeight, std::string savedImagePath);

    static cv::String sendDataToJava(cv::Mat mat, std::string basicString, int64_t time);

    static void getVisitorDetails();

private:
    // Read pixels from FBO texture to CV image
    void CreateRGBAImageFromGLFBOTexture(int textureWidth, int textureHeight,
                                         cv::Mat *rgbaImage,
                                         double *readGLFBOTime) {
        auto t = GetCurrentTime();
        rgbaImage->create(textureHeight, textureWidth, CV_8UC4);
        glReadPixels(0, 0, textureWidth, textureHeight, GL_RGBA, GL_UNSIGNED_BYTE,
                     rgbaImage->data);
        *readGLFBOTime = GetElapsedTime(t);
        LOGD("Read from FBO texture costs %f ms", *readGLFBOTime);
    }

    // Write back to texture2D
    void WriteRGBAImageBackToGLTexture(const cv::Mat &rgbaImage, int textureId,
                                       double *writeGLTextureTime) {
        auto t = GetCurrentTime();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, rgbaImage.cols, rgbaImage.rows,
                        GL_RGBA, GL_UNSIGNED_BYTE, rgbaImage.data);
        *writeGLTextureTime = GetElapsedTime(t);
        LOGD("Write back to texture2D costs %f ms", *writeGLTextureTime);
    }

    // Visualize the results to origin image
    void VisualizeResults(const std::vector<Face> &faces, cv::Mat *rgbaImage);

    // Visualize the status(performace data) to origin image
    void VisualizeStatus(double readGLFBOTime, double writeGLTextureTime,
                         double fdtPreprocessTime, double fdtPredictTime,
                         double fdtPostprocessTime, double mclPreprocessTime,
                         double mclPredictTime, double mclPostprocessTime,
                         cv::Mat *rgbaImage);

private:
    std::shared_ptr<FaceDetector> faceDetector_;
    std::shared_ptr<MaskClassifier> maskClassifier_;

};

/*extern "C" int LinkToFirebase(int argc, const char* argv[]) {

    ::firebase::App *app;
    using app_framework::LogMessage;

#if defined(__ANDROID__)
    app = ::firebase::App::Create(app_framework::GetJniEnv(),
                                  app_framework::GetActivity());
#else
    app = ::firebase::App::Create();
#endif  // defined(__ANDROID__)
    __android_log_print(ANDROID_LOG_INFO, "Firebase storage", "%s", "Initialization done!!");

    ::firebase::storage::Storage *storage = firebase::storage::Storage::GetInstance(app);

// Points to the root reference
    firebase::storage::StorageReference storage_ref = storage->GetReferenceFromUrl(
            "gs://covidsafe-279814.appspot.com");

// Points to "images"
    firebase::storage::StorageReference images_ref = storage_ref.Child("images");

    __android_log_print(ANDROID_LOG_INFO, "Firebase storage details", "%s",
                        storage_ref.name().c_str());

// Points to "images/space.jpg"
// Note that you can use variables to create child values
    std::string filename = "space.jpg";
    firebase::storage::StorageReference space_ref = images_ref.Child(filename);

// File path is "images/space.jpg"
    std::string path = space_ref.full_path();

// File name is "space.jpg"
    std::string name = space_ref.name();

// Points to "images"
    images_ref = space_ref.GetParent();

    return 0;


}*/







