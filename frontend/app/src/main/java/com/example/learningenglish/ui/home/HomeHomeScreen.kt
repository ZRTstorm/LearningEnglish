package com.example.learningenglish.ui.home

import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.layout.*
import com.skydoves.landscapist.*
import com.skydoves.landscapist.coil3.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.geometry.Offset
import androidx.navigation.NavController
import com.example.learningenglish.viewmodel.AuthViewModel
import com.example.learningenglish.viewmodel.LearningViewModel

@Composable
fun HomeHomeScreen(navController: NavController,
                    viewModel: AuthViewModel,
                    learningViewModel: LearningViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                color = Color(0xFFFFFFFF),
            )
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF645BD8), Color(0xFF4139BF), ),
                        start = Offset.Zero,
                        end = Offset(0F,Float.POSITIVE_INFINITY),
                    )
                )
                .shadow(
                    elevation = 4.dp,
                    spotColor = Color(0x40000000),
                )
                .verticalScroll(rememberScrollState())
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 32.dp,bottom = 21.dp,start = 24.dp,end = 24.dp,)
                    .fillMaxWidth()
            ){
                Text("LearningApp",
                    color = Color(0xFFFFFFFF),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                )
                CoilImage(
                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/GTIoy1A99q/1mwopdg9_expires_30_days.png"},
                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                    modifier = Modifier
                        .width(37.dp)
                        .height(30.dp)
                )
            }
            Column(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(42.dp))
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF6F6F6),
                        shape = RoundedCornerShape(42.dp)
                    )
                    .padding(top = 18.dp,)
            ){
                OutlinedButton(
                    onClick = { println("Pressed!") },
                    border = BorderStroke(0.dp, Color.Transparent),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(),
                    modifier = Modifier
                        .padding(bottom = 15.dp,start = 24.dp,)
                        .border(
                            width = 1.dp,
                            color = Color(0xFFE67F7F),
                            shape = RoundedCornerShape(13.dp)
                        )
                        .clip(shape = RoundedCornerShape(13.dp))
                        .background(
                            color = Color(0xFFFCE4E4),
                            shape = RoundedCornerShape(13.dp)
                        )
                        .shadow(
                            elevation = 4.dp,
                            spotColor = Color(0x40000000),
                        )
                ){
                    Column(
                        modifier = Modifier
                            .padding(vertical = 6.dp,horizontal = 24.dp,)
                    ){
                        Text("2일째 학습 중",
                            color = Color(0xFFDF6FAC),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(bottom = 11.dp,start = 28.dp,)
                ){
                    CoilImage(
                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/GTIoy1A99q/dtdhcjsq_expires_30_days.png"},
                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                        modifier = Modifier
                            .padding(end = 27.dp,)
                            .width(88.dp)
                            .height(80.dp)
                    )
                    Text(" 옴냐냐님",
                        color = Color(0xFF000000),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 15.dp,end = 10.dp,)
                    )
                    Text("어서 오세요",
                        color = Color(0xFF000000),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 47.dp,)
                    )
                }
                Box{
                    CoilImage(
                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/GTIoy1A99q/azcua94w_expires_30_days.png"},
                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                        modifier = Modifier
                            .padding(bottom = 9.dp,)
                            .fillMaxWidth()
                    )
                    Column(
                        modifier = Modifier
                            .padding(bottom = 9.dp,)
                            .fillMaxWidth()
                            .padding(top = 32.dp,bottom = 236.dp,)
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(horizontal = 44.dp,)
                                .clip(shape = RoundedCornerShape(16.dp))
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xE3F2EBEB),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(top = 21.dp,bottom = 21.dp,start = 42.dp,end = 12.dp,)
                        ){
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(end = 12.dp,)
                                    .weight(1f)
                            ){
                                Text("최근 학습 ",
                                    color = Color(0xFF000000),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(bottom = 13.dp,)
                                )
                                Text("Is Minecraft?",
                                    color = Color(0xFF848484),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(17.dp))
                                    .background(
                                        color = Color(0x80FFFFFF),
                                        shape = RoundedCornerShape(17.dp)
                                    )
                                    .padding(top = 11.dp,bottom = 11.dp,start = 13.dp,end = 5.dp,)
                            ){
                                Text("이어 하기",
                                    color = Color(0xFF000000),
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .padding(end = 11.dp,)
                                )
                                CoilImage(
                                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/GTIoy1A99q/ydxh3h1h_expires_30_days.png"},
                                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                    modifier = Modifier
                                        .width(19.dp)
                                        .height(19.dp)
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(bottom = 1.dp,start = 188.dp,)
                ){
                    CoilImage(
                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/GTIoy1A99q/qx9nc9rf_expires_30_days.png"},
                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                        modifier = Modifier
                            .padding(end = 28.dp,)
                            .width(12.dp)
                            .height(12.dp)
                    )
                    CoilImage(
                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/GTIoy1A99q/oputjch6_expires_30_days.png"},
                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                        modifier = Modifier
                            .width(12.dp)
                            .height(12.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp,)
                ){
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 10.dp,)
                            .border(
                                width = 1.dp,
                                color = Color(0xFFF3CCCC),
                                shape = RoundedCornerShape(22.dp)
                            )
                            .clip(shape = RoundedCornerShape(22.dp))
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFEFEFF0),
                                shape = RoundedCornerShape(22.dp)
                            )
                            .padding(top = 13.dp,bottom = 32.dp,)
                    ){
                        Text(" 출석",
                            color = Color(0xFF000000),
                            fontSize = 24.sp,
                            modifier = Modifier
                                .padding(bottom = 6.dp,start = 168.dp,)
                        )
                        OutlinedButton(
                            onClick = { println("Pressed!") },
                            border = BorderStroke(0.dp, Color.Transparent),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(),
                            modifier = Modifier
                                .padding(bottom = 4.dp,start = 153.dp,)
                                .clip(shape = RoundedCornerShape(4.dp))
                                .background(
                                    color = Color(0xFF000000),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        ){
                            Column(
                                modifier = Modifier
                                    .padding(vertical = 5.dp,horizontal = 7.dp,)
                            ){
                                Text("June",
                                    color = Color(0xFFFFFFFF),
                                    fontSize = 11.sp,
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .padding(bottom = 13.dp,start = 17.dp,)
                        ){
                            Text("Mo",
                                color = Color(0xFFB3B3B3),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 5.dp,bottom = 5.dp,start = 3.dp,end = 37.dp,)
                            )
                            Text("Tu",
                                color = Color(0xFFB3B3B3),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 5.dp,bottom = 5.dp,start = 5.dp,end = 39.dp,)
                            )
                            Text("We",
                                color = Color(0xFFB3B3B3),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 5.dp,bottom = 5.dp,start = 3.dp,end = 37.dp,)
                            )
                            Text("Th",
                                color = Color(0xFFB3B3B3),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 5.dp,bottom = 5.dp,start = 4.dp,end = 38.dp,)
                            )
                            Text("Fr",
                                color = Color(0xFFB3B3B3),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 5.dp,bottom = 5.dp,start = 5.dp,end = 39.dp,)
                            )
                            Text("Sa",
                                color = Color(0xFF16A0F9),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 5.dp,bottom = 5.dp,start = 4.dp,end = 37.dp,)
                            )
                            Text("Su",
                                color = Color(0xFF16A0F9),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(5.dp)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(bottom = 13.dp,start = 17.dp,)
                        ){
                            Text("26",
                                color = Color(0xFF606060),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 5.dp,bottom = 5.dp,start = 4.dp,end = 38.dp,)
                            )
                            Text("27",
                                color = Color(0xFF606060),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 5.dp,bottom = 5.dp,start = 5.dp,end = 39.dp,)
                            )
                            Text("28",
                                color = Color(0xFF606060),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 5.dp,bottom = 5.dp,start = 4.dp,end = 38.dp,)
                            )
                            Text("29",
                                color = Color(0xFF606060),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 5.dp,bottom = 5.dp,start = 4.dp,end = 38.dp,)
                            )
                            Text("30",
                                color = Color(0xFF606060),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 5.dp,bottom = 5.dp,start = 4.dp,end = 38.dp,)
                            )
                            Text("31",
                                color = Color(0xFF1170AF),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 5.dp,bottom = 5.dp,start = 4.dp,end = 37.dp,)
                            )
                            Text("1",
                                color = Color(0xFF16A0F9),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(vertical = 5.dp,horizontal = 9.dp,)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(bottom = 3.dp,start = 17.dp,)
                        ){
                            Text("2",
                                color = Color(0xFFB3B3B3),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 5.dp,bottom = 5.dp,start = 8.dp,end = 42.dp,)
                            )
                            Text("3",
                                color = Color(0xFFB3B3B3),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 5.dp,bottom = 5.dp,start = 7.dp,end = 41.dp,)
                            )
                            Text("4",
                                color = Color(0xFFB3B3B3),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 5.dp,bottom = 5.dp,start = 7.dp,end = 41.dp,)
                            )
                            Text("5",
                                color = Color(0xFFB3B3B3),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 5.dp,bottom = 5.dp,start = 7.dp,end = 41.dp,)
                            )
                            Text("6",
                                color = Color(0xFFB3B3B3),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 5.dp,bottom = 5.dp,start = 7.dp,end = 41.dp,)
                            )
                            Text("7",
                                color = Color(0xFF16A0F9),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 5.dp,bottom = 5.dp,start = 7.dp,end = 40.dp,)
                            )
                            Text("8",
                                color = Color(0xFF16A0F9),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(vertical = 5.dp,horizontal = 8.dp,)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(start = 25.dp,)
                        ){
                            Text("9",
                                color = Color(0xFFB3B3B3),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 16.dp,end = 50.dp,)
                            )
                            Text("10",
                                color = Color(0xFFB3B3B3),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 16.dp,end = 48.dp,)
                            )
                            Text("11",
                                color = Color(0xFFB3B3B3),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 16.dp,end = 16.dp,)
                            )
                            CoilImage(
                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/GTIoy1A99q/vszewxha_expires_30_days.png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .padding(end = 15.dp,)
                                    .width(74.dp)
                                    .height(74.dp)
                            )
                            Text("13",
                                color = Color(0xFFB3B3B3),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 16.dp,end = 47.dp,)
                            )
                            Text("14",
                                color = Color(0xFF16A0F9),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 16.dp,end = 48.dp,)
                            )
                            Text("15",
                                color = Color(0xFF16A0F9),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(top = 16.dp,)
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(29.dp))
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(29.dp)
                    )
                    .shadow(
                        elevation = 4.dp,
                        spotColor = Color(0x03000000),
                    )
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 9.dp,bottom = 5.dp,start = 30.dp,end = 30.dp,)
                        .height(24.dp)
                        .fillMaxWidth()
                ){
                    CoilImage(
                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/GTIoy1A99q/crug8vku_expires_30_days.png"},
                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                        modifier = Modifier
                            .padding(end = 57.dp,)
                            .width(24.dp)
                            .height(24.dp)
                    )
                    CoilImage(
                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/GTIoy1A99q/67vaydom_expires_30_days.png"},
                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                        modifier = Modifier
                            .width(30.dp)
                            .height(34.dp)
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ){
                    }
                    CoilImage(
                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/GTIoy1A99q/7222j855_expires_30_days.png"},
                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                        modifier = Modifier
                            .padding(end = 50.dp,)
                            .width(24.dp)
                            .height(24.dp)
                    )
                    CoilImage(
                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/GTIoy1A99q/aenwfa2h_expires_30_days.png"},
                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                        modifier = Modifier
                            .width(24.dp)
                            .height(24.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(bottom = 18.dp,start = 35.dp,)
                ){
                    Text("홈",
                        color = Color(0xFF000000),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(end = 44.dp,)
                    )
                    Text("라이브러리",
                        color = Color(0xFF000000),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(end = 114.dp,)
                    )
                    Text("검색",
                        color = Color(0xFF000000),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(end = 45.dp,)
                    )
                    Text("단어장",
                        color = Color(0xFF000000),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}