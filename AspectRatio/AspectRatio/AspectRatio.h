
// AspectRatio.h: главный файл заголовка для приложения PROJECT_NAME
//

#pragma once

#ifndef __AFXWIN_H__
	#error "включить pch.h до включения этого файла в PCH"
#endif

#include "resource.h"		// основные символы


// CAspectRatioApp:
// Сведения о реализации этого класса: AspectRatio.cpp
//

class CAspectRatioApp : public CWinApp
{
public:
	CAspectRatioApp();

// Переопределение
public:
	virtual BOOL InitInstance();

// Реализация

	DECLARE_MESSAGE_MAP()
};

extern CAspectRatioApp theApp;
