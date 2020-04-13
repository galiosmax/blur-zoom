
// AspectRatioDlg.h: файл заголовка
//

#pragma once
#include "CPicture.h"


// Диалоговое окно CAspectRatioDlg
class CAspectRatioDlg : public CDialogEx
{
// Создание
public:
	CAspectRatioDlg(CWnd* pParent = nullptr);	// стандартный конструктор

// Данные диалогового окна
#ifdef AFX_DESIGN_TIME
	enum { IDD = IDD_ASPECTRATIO_DIALOG };
#endif

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// поддержка DDX/DDV


// Реализация
protected:
	HICON m_hIcon;
	HICON m_playIcon;
	HICON m_pauseIcon;
	HICON m_stopIcon;

	HANDLE m_fileHandler;
	bool m_isPlaying;
	bool m_isFile;
	double m_fps;
	long m_timer = 0;
	long m_previousFrame;
	CComPtr<IGetAVIInfo> m_ptr;
	MainAVIHeader mainHeader;
	BITMAPINFO m_bmpOut;
	BITMAPINFO* m_bmpIn;
	BYTE* m_buffer;
	RGBQUAD* m_frame;
	HIC m_hic;


	// Созданные функции схемы сообщений
	virtual BOOL OnInitDialog();
	afx_msg void OnSysCommand(UINT nID, LPARAM lParam);
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	DECLARE_MESSAGE_MAP()
public:
	CMenu m_menu;
	CButton m_playButton;
	CButton m_stopButton;
	CSliderCtrl m_slider;
	CPicture m_picture;
	afx_msg void OnBnClickedPlayPause();
	afx_msg void OnBnClickedStop();
	afx_msg void OnNMCustomdrawSlider(NMHDR* pNMHDR, LRESULT* pResult);
	afx_msg void OnFileOpen();
	afx_msg void OnFileExit();
	afx_msg void OnPlaybackPlay();
	afx_msg void OnPlaybackStop();
	afx_msg void OnTimer(UINT_PTR nIDEvent);
	void SetImage(long frameNumber);
	void OnPaintImpl();
	int OpenFile(CString path);

};
