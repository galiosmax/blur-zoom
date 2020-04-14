
// AspectRatioDlg.cpp: файл реализации
//

#include "pch.h"
#include "framework.h"
#include "AspectRatio.h"
#include "AspectRatioDlg.h"
#include "afxdialogex.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// Диалоговое окно CAboutDlg используется для описания сведений о приложении

class CAboutDlg : public CDialogEx
{
public:
	CAboutDlg();

	// Данные диалогового окна
#ifdef AFX_DESIGN_TIME
	enum { IDD = IDD_ABOUTBOX };
#endif

protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // поддержка DDX/DDV

// Реализация
protected:
	DECLARE_MESSAGE_MAP()
};

CAboutDlg::CAboutDlg() : CDialogEx(IDD_ABOUTBOX)
{
}

void CAboutDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
}

BEGIN_MESSAGE_MAP(CAboutDlg, CDialogEx)
END_MESSAGE_MAP()


// Диалоговое окно CAspectRatioDlg



CAspectRatioDlg::CAspectRatioDlg(CWnd* pParent /*=nullptr*/)
	: CDialogEx(IDD_ASPECTRATIO_DIALOG, pParent)
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
	m_playIcon = AfxGetApp()->LoadIcon(IDI_ICON_PLAY);
	m_pauseIcon = AfxGetApp()->LoadIcon(IDI_ICON_PAUSE);
	m_stopIcon = AfxGetApp()->LoadIcon(IDI_ICON_STOP);

	m_isPlaying = false;
}

void CAspectRatioDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_PLAY_PAUSE, m_playButton);
	DDX_Control(pDX, IDC_STOP, m_stopButton);
	DDX_Control(pDX, IDC_SLIDER, m_slider);
	DDX_Control(pDX, IDC_PICTURE, m_picture);
}

BEGIN_MESSAGE_MAP(CAspectRatioDlg, CDialogEx)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_WM_TIMER()
	ON_BN_CLICKED(IDC_PLAY_PAUSE, &CAspectRatioDlg::OnBnClickedPlayPause)
	ON_BN_CLICKED(IDC_STOP, &CAspectRatioDlg::OnBnClickedStop)
	ON_NOTIFY(NM_CUSTOMDRAW, IDC_SLIDER, &CAspectRatioDlg::OnNMCustomdrawSlider)
	ON_COMMAND(ID_FILE_OPEN, &CAspectRatioDlg::OnFileOpen)
	ON_COMMAND(ID_FILE_EXIT, &CAspectRatioDlg::OnFileExit)
	ON_COMMAND(ID_PLAYBACK_PLAY, &CAspectRatioDlg::OnPlaybackPlay)
	ON_COMMAND(ID_PLAYBACK_STOP, &CAspectRatioDlg::OnPlaybackStop)
END_MESSAGE_MAP()


// Обработчики сообщений CAspectRatioDlg

BOOL CAspectRatioDlg::OnInitDialog()
{
	CDialogEx::OnInitDialog();

	// Добавление пункта "О программе..." в системное меню.

	// IDM_ABOUTBOX должен быть в пределах системной команды.
	ASSERT((IDM_ABOUTBOX & 0xFFF0) == IDM_ABOUTBOX);
	ASSERT(IDM_ABOUTBOX < 0xF000);

	CMenu* pSysMenu = GetSystemMenu(TRUE);
	if (pSysMenu != nullptr)
	{
		BOOL bNameValid;
		CString strAboutMenu;
		bNameValid = strAboutMenu.LoadString(IDS_ABOUTBOX);
		ASSERT(bNameValid);
		if (!strAboutMenu.IsEmpty())
		{
			pSysMenu->AppendMenu(MF_SEPARATOR);
			pSysMenu->AppendMenu(MF_STRING, IDM_ABOUTBOX, strAboutMenu);
		}
	}

	// Задает значок для этого диалогового окна.  Среда делает это автоматически,
	//  если главное окно приложения не является диалоговым
	SetIcon(m_hIcon, TRUE);			// Крупный значок
	SetIcon(m_hIcon, FALSE);		// Мелкий значок

	m_slider.SetRange(0, 255);

	m_playButton.SetIcon(m_playIcon);
	m_stopButton.SetIcon(m_stopIcon);

	m_isPlaying = false;
	m_isFile = false;

	SetDlgItemText(IDC_TIMER, L"00:00:00");
	SetDlgItemText(IDC_TOTAL, L"00:00:00");

	m_menu.LoadMenuW(MAKEINTRESOURCE(IDR_MENU));
	SetMenu(&m_menu);
	DrawMenuBar();

	return TRUE;  // возврат значения TRUE, если фокус не передан элементу управления
}

void CAspectRatioDlg::OnSysCommand(UINT nID, LPARAM lParam)
{
	if ((nID & 0xFFF0) == IDM_ABOUTBOX)
	{
		CAboutDlg dlgAbout;
		dlgAbout.DoModal();
	}
	else
	{
		CDialogEx::OnSysCommand(nID, lParam);
	}
}

// При добавлении кнопки свертывания в диалоговое окно нужно воспользоваться приведенным ниже кодом,
//  чтобы нарисовать значок.  Для приложений MFC, использующих модель документов или представлений,
//  это автоматически выполняется рабочей областью.

void CAspectRatioDlg::OnPaint()
{
	if (!IsIconic())
	{
		CDialogEx::OnPaint();
		return;
	}
	CPaintDC dc(this); // контекст устройства для рисования

	SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

	// Выравнивание значка по центру клиентского прямоугольника
	int cxIcon = GetSystemMetrics(SM_CXICON);
	int cyIcon = GetSystemMetrics(SM_CYICON);
	CRect rect;
	GetClientRect(&rect);
	int x = (rect.Width() - cxIcon + 1) / 2;
	int y = (rect.Height() - cyIcon + 1) / 2;

	// Нарисуйте значок
	dc.DrawIcon(x, y, m_hIcon);
}

// Система вызывает эту функцию для получения отображения курсора при перемещении
//  свернутого окна.
HCURSOR CAspectRatioDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}



void CAspectRatioDlg::OnBnClickedPlayPause()
{
	m_isPlaying = !m_isPlaying;
	m_playButton.SetIcon(m_isPlaying ? m_pauseIcon : m_playIcon);

	//SetTimer(1, (int)(1000.0 / m_fps), NULL);
	SetTimer(1, 1000.0, NULL);
}


void CAspectRatioDlg::OnBnClickedStop()
{
	m_isPlaying = false;
	m_timer = 0;
	m_playButton.SetIcon(m_playIcon);
	OnPaintImpl();
}


void CAspectRatioDlg::OnNMCustomdrawSlider(NMHDR* pNMHDR, LRESULT* pResult)
{
	LPNMCUSTOMDRAW pNMCD = reinterpret_cast<LPNMCUSTOMDRAW>(pNMHDR);

	m_timer = m_slider.GetPos();
	OnPaintImpl();

	*pResult = 0;
}


void CAspectRatioDlg::OnFileOpen()
{
	CFileDialog dialog(TRUE, NULL, NULL, OFN_PATHMUSTEXIST | OFN_FILEMUSTEXIST, L"AVI Files (*.avi)|*.avi|", NULL, 0, TRUE);

	if (dialog.DoModal() == IDOK)
	{
		CString path = dialog.GetPathName();
		OpenFile(path);

		ATLASSERT(ICDecompressBegin(m_hic, &(m_bmpIn->bmiHeader), &(m_bmpOut.bmiHeader)) == S_OK);

		m_picture.Init(m_bmpOut);

		m_isFile = true;
		OnBnClickedPlayPause();
		m_timer = 0;
	}
}

int CAspectRatioDlg::OpenFile(CString path)
{
	m_fileHandler = CreateFile(path, GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);

	if (m_fileHandler == INVALID_HANDLE_VALUE)
	{
		return 1;
	}

	Initialize();

	ATLASSERT(m_ptr.CoCreateInstance(GETAVIINFOLib::CLSID_GetAVIInfo) == S_OK);

	CComBSTR fileName(path);
	ATLASSERT(m_ptr->FastLoad(fileName) == S_OK);

	long copy_size = 0;
	MainAVIHeader mainHeader;
	ATLASSERT(m_ptr->GetMainHeader(reinterpret_cast<BYTE*>(&mainHeader), sizeof(MainAVIHeader), &copy_size) == S_OK);

	long size_info, size_infoex;
	AVIStreamHeader streamHeader;
	ATLASSERT(m_ptr->GetVideoStreamInfo(0, reinterpret_cast<BYTE*>(&streamHeader), sizeof(AVIStreamHeader), &copy_size, &size_info, &size_infoex) == S_OK);

	m_bmpIn = new BITMAPINFO[size_info];
	ATLASSERT(m_ptr->GetVideoFormatInfo(0, reinterpret_cast<BYTE*>(m_bmpIn), size_info, &copy_size) == S_OK);

	memcpy(&m_bmpOut, m_bmpIn, sizeof(BITMAPINFOHEADER));

	int size = m_bmpOut.bmiHeader.biHeight * m_bmpOut.bmiHeader.biWidth;

	m_bmpOut.bmiHeader.biBitCount = 32;
	m_bmpOut.bmiHeader.biCompression = BI_RGB;
	m_bmpOut.bmiHeader.biSizeImage = 4 * size;
	m_bmpOut.bmiHeader.biClrUsed = 0;

	m_buffer = new BYTE[streamHeader.dwSuggestedBufferSize];
	m_bmpIn->bmiHeader.biSizeImage = streamHeader.dwSuggestedBufferSize;
	m_frame = new RGBQUAD[size];

	DWORD vidc = MAKEFOURCC('v', 'i', 'd', 'c');

	m_hic = ICOpen(vidc, streamHeader.fccHandler, ICMODE_FASTDECOMPRESS);
	if (!m_hic)
	{
		m_hic = ICLocate(vidc, streamHeader.fccHandler, &(m_bmpIn->bmiHeader), &(m_bmpOut.bmiHeader), ICMODE_FASTDECOMPRESS);
		if (!m_hic)
		{
			return 1;
		}
	}

	m_fps = streamHeader.dwRate / streamHeader.dwScale;
	m_slider.SetRange(0, mainHeader.dwTotalFrames - 1);
	int seconds = mainHeader.dwTotalFrames / m_fps;
	int hours = seconds / 3600;
	int minutes = (seconds % 3600) / 60;

	wchar_t buffer[32];
	wsprintfW(buffer, L"%02d:%02d:%02d", hours, minutes, seconds % 60);
	SetDlgItemText(IDC_TOTAL, buffer);

	return 0;
}

void CAspectRatioDlg::OnFileExit()
{
	PostQuitMessage(0);
}


void CAspectRatioDlg::OnPlaybackPlay()
{
	OnBnClickedPlayPause();
}


void CAspectRatioDlg::OnPlaybackStop()
{
	OnBnClickedStop();
}

void CAspectRatioDlg::OnTimer(UINT_PTR nIDEvent)
{
	if (!m_isPlaying)
	{
		return;
	}
	m_isPlaying = !(m_timer == m_slider.GetRangeMax());
	if (m_isPlaying)
	{
		OnPaintImpl();
		m_timer++;
	}

	CDialog::OnTimer(nIDEvent);
}

void CAspectRatioDlg::OnPaintImpl()
{
	if (m_isFile)
	{
		SetImage(m_timer);
		m_picture.Update(m_frame);

		int seconds = m_timer / m_fps;
		int hours = seconds / 3600;
		int minutes = (seconds % 3600) / 60;

		wchar_t buffer[32];
		wsprintfW(buffer, L"%02d:%02d:%02d", hours, minutes, seconds % 60);
		SetDlgItemText(IDC_TIMER, buffer);
	}

	m_slider.SetPos(m_timer);
}


void CAspectRatioDlg::SetImage(long frameNumber)
{
	if (frameNumber == m_previousFrame && frameNumber != 0)
	{
		return;
	}

	ULONG flags;
	ULONG size;
	unsigned long offset;

	ATLASSERT(m_ptr->GetVideoFrameInfo2(0, frameNumber, 0, &offset, &size, &flags) == S_OK);
	if ((m_previousFrame != 0 && m_previousFrame != frameNumber - 1) && flags != 16)
	{
		SetImage(frameNumber - 1);
	}

	ATLASSERT(SetFilePointer(m_fileHandler, offset, 0, SEEK_SET) != INVALID_SET_FILE_POINTER);
	ATLASSERT(ReadFile(m_fileHandler, m_buffer, size, &size, NULL) != 0);
	ATLASSERT(ICDecompress(m_hic, flags, &(m_bmpIn->bmiHeader), m_buffer, &(m_bmpOut.bmiHeader), m_frame) == ICERR_OK);

	m_previousFrame = frameNumber;
}