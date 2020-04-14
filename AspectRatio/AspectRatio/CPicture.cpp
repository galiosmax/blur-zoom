#include "pch.h"
#include "CPicture.h"
#include <ctime>
#include <iostream>
#include <cmath>

const double CPicture::m_proportion = 16.0 / 9.0;

CPicture::~CPicture()
{
	delete[] m_stretchFrame;
	delete[] m_gaussianFrame;
}

void CPicture::Init(BITMAPINFO bmp_new)
{
	m_bmp = bmp_new;
	m_bmpOut = bmp_new;
	m_bmpGauss = bmp_new;

	this->GetWindowRect(&m_rect);

	int size = m_rect.Width() * m_rect.Height();
	m_bmpOut.bmiHeader.biWidth = m_rect.Width();
	m_bmpOut.bmiHeader.biHeight = m_rect.Height();
	m_bmpOut.bmiHeader.biSizeImage = sizeof(RGBQUAD) * size;

	m_stretchFrame = new RGBQUAD[size];

	int height = m_bmp.bmiHeader.biHeight;
	int width = (int)floor(height * m_proportion);
	int gaussSize = height * width;

	m_bmpGauss.bmiHeader.biWidth = width;
	m_bmpGauss.bmiHeader.biSizeImage = sizeof(RGBQUAD) * size;

	m_gaussianFrame = new RGBQUAD[gaussSize];

	ATLASSERT(m_pScaler.CoCreateInstance(ICONVERTIMAGELib::CLSID_StretchImage) == S_OK);
	m_init = true;
}

BEGIN_MESSAGE_MAP(CPicture, CWnd)
	ON_WM_PAINT()
END_MESSAGE_MAP()


void CPicture::OnPaint()
{
	CPaintDC dc(this); // device context for painting

	if (!m_init)
	{
		return;
	}

	CreateGaussianFrame();
	HRESULT hr = m_pScaler->StretchImage(reinterpret_cast<unsigned char*>(m_stretchFrame), reinterpret_cast<unsigned char*>(m_gaussianFrame),
		m_bmpOut.bmiHeader.biWidth, m_bmpOut.bmiHeader.biHeight, m_bmpGauss.bmiHeader.biWidth, m_bmpGauss.bmiHeader.biHeight,
		m_bmpOut.bmiHeader.biWidth* m_bmp.bmiHeader.biBitCount / 8, m_bmpGauss.bmiHeader.biWidth* m_bmpOut.bmiHeader.biBitCount / 8, 8);

	ATLASSERT(hr == S_OK);
	SetDIBitsToDevice(dc, 0, 0, m_rect.Width(), m_rect.Height(), 0, 0, 0, m_rect.Height(), m_stretchFrame, &m_bmp, DIB_RGB_COLORS);;
}

void CPicture::Update(RGBQUAD* frame)
{
	m_frame = frame;
	Invalidate();
}

void CPicture::CreateGaussianFrame()
{
	unsigned int start_time = clock();

	int height = m_bmpGauss.bmiHeader.biHeight;
	int width = m_bmpGauss.bmiHeader.biWidth;
	int originalWidth = m_bmp.bmiHeader.biWidth;

	int leftWidth = (width - originalWidth) / 2;

	RGBQUAD* toProcessLeft = CreateSideImage(width, height, originalWidth, true);
	RGBQUAD* toProcessRight = CreateSideImage(width, height, originalWidth, false);

	RGBQUAD* processedLeft = ProcessImage(toProcessLeft, leftWidth, height);
	RGBQUAD* processedRight = ProcessImage(toProcessRight, width - leftWidth - originalWidth, height);

	BuildGaussImage(width, height, m_frame, leftWidth, processedLeft, width - leftWidth - originalWidth, processedRight);

	delete[] toProcessLeft;
	delete[] toProcessRight;
	delete[] processedLeft;
	delete[] processedRight;

	printf("Gausszoom took %d ms", clock() - start_time);
}

RGBQUAD* CPicture::CreateSideImage(int width, int height, int originalWidth, boolean left)
{
	int subImageWidth = (width - originalWidth) / 4;
	int subImageHeight = height / 2;

	if (left)
	{
		return CreateSubImage(m_frame, 0, subImageHeight / 2, subImageWidth, subImageHeight);
	}
	else
	{
		return CreateSubImage(m_frame, originalWidth - subImageWidth - 1, subImageHeight / 2, subImageWidth, subImageHeight);
	}
}

RGBQUAD* CPicture::CreateSubImage(RGBQUAD* original, int x, int y, int width, int height)
{
	RGBQUAD* subImage = new RGBQUAD[width * height];
	int bytes = width * sizeof(RGBQUAD);

	for (int j = 0; j < height; ++j)
	{
		memcpy(&subImage[j * width], &original[x + (y + j) * m_bmp.bmiHeader.biWidth], bytes);
	}

	return subImage;
}

RGBQUAD* CPicture::ProcessImage(RGBQUAD* image, int width, int height)
{
	return Gauss(Zoom(image, width, height, 2), width, height, 3);
}

void CPicture::BuildGaussImage(int width, int height, RGBQUAD* originalImage, int leftWidth, RGBQUAD* leftSide, int rightWidth, RGBQUAD* rightSide)
{
	int originalWidth = m_bmp.bmiHeader.biWidth;
	int leftBytes = leftWidth * sizeof(RGBQUAD);
	int originalBytes = originalWidth * sizeof(RGBQUAD);
	int rightBytes = rightWidth * sizeof(RGBQUAD);

	for (int j = 0; j < height; ++j)
	{
		int offset = j * width;
		memcpy(&m_gaussianFrame[offset], &leftSide[j * leftWidth], leftBytes);
		memcpy(&m_gaussianFrame[leftWidth + offset], &originalImage[j * originalWidth], originalBytes);
		memcpy(&m_gaussianFrame[offset + width - rightWidth - 1], &rightSide[j * rightWidth], rightBytes);
	}
}

RGBQUAD* CPicture::Zoom(RGBQUAD* image, int width, int height, int times)
{
	RGBQUAD* zoomed = new RGBQUAD[width * height * times * times];

	int multWidth = width * times;

	for (int y = 0; y < height; ++y)
	{
		int yOffset = y * width;
		int mult = y * times;

		for (int x = 0; x < width; ++x)
		{
			int imageIndex = yOffset + x;
			RGBQUAD first = image[imageIndex];
			RGBQUAD second = x < width - 1 ? image[imageIndex + 1] : first;

			int red = first.rgbRed;
			int green = first.rgbGreen;
			int blue = first.rgbBlue;

			int dR = (second.rgbRed - first.rgbRed) / times;
			int dG = (second.rgbGreen - first.rgbGreen) / times;
			int dB = (second.rgbBlue - first.rgbBlue) / times;

			int xOffset = x * times;

			for (int k = 0; k < times; ++k)
			{
				int index = xOffset + (k + mult) * multWidth;
				zoomed[index].rgbRed = red;
				zoomed[index].rgbGreen = green;
				zoomed[index].rgbBlue = blue;

				red += dR;
				green += dG;
				blue += dB;
			}
		}
	}

	for (int y = 0; y < height; ++y)
	{
		int yOffset = y * times * multWidth;

		for (int x = 0; x < multWidth; ++x)
		{
			int zoomedIndex = yOffset + x;
			RGBQUAD first = zoomed[zoomedIndex];
			RGBQUAD second = y < height - 1 ? zoomed[x + times * multWidth + yOffset] : first;

			int red = first.rgbRed;
			int green = first.rgbGreen;
			int blue = first.rgbBlue;

			int dR = (second.rgbRed - first.rgbRed) / times;
			int dG = (second.rgbGreen - first.rgbGreen) / times;
			int dB = (second.rgbBlue - first.rgbBlue) / times;

			for (int k = 0; k < times; ++k)
			{
				int index = x + k * multWidth + yOffset;
				zoomed[index].rgbRed = red;
				zoomed[index].rgbGreen = green;
				zoomed[index].rgbBlue = blue;

				red += dR;
				green += dG;
				blue += dB;
			}
		}
	}

	return zoomed;
}

RGBQUAD* CPicture::Gauss(RGBQUAD* image, int width, int height, int radius)
{
	int size = width * height;

	int* redSource = new int[size];
	int* greenSource = new int[size];
	int* blueSource = new int[size];

	for (int i = 0; i < size; ++i)
	{
		RGBQUAD color = image[i];
		redSource[i] = color.rgbRed;
		greenSource[i] = color.rgbGreen;
		blueSource[i] = color.rgbBlue;
	}

	int* redDest = new int[size];
	int* greenDest = new int[size];
	int* blueDest = new int[size];

	gaussBlur_4(redSource, redDest, width, height, radius);
	gaussBlur_4(greenSource, greenDest, width, height, radius);
	gaussBlur_4(blueSource, blueDest, width, height, radius);

	RGBQUAD* res = new RGBQUAD[size];

	for (int i = 0; i < size; ++i)
	{
		res[i].rgbRed = min(255, max(0, redDest[i]));
		res[i].rgbGreen = min(255, max(0, greenDest[i]));
		res[i].rgbBlue = min(255, max(0, blueDest[i]));
	}

	delete[] redSource;
	delete[] greenSource;
	delete[] blueSource;
	delete[] redDest;
	delete[] greenDest;
	delete[] blueDest;

	return res;
}

int* CPicture::boxesForGauss(float sigma, int n)
{
	double sigma_12 = 12.0 * sigma * sigma;

	double wIdeal = sqrt((sigma_12 / n) + 1);
	int wl = (int)floor(wIdeal);
	if (wl % 2 == 0)
	{
		wl--;
	}
	int wu = wl + 2;

	int minus_4_wl = -4 * wl;

	double mIdeal = (double)(sigma_12 - n * wl * wl + minus_4_wl * n - 3 * n) / (minus_4_wl - 4);
	int m = (int)round(mIdeal);

	int* sizes = new int[n];
	for (int i = 0; i < n; ++i)
	{
		sizes[i] = i < m ? wl : wu;
	}

	return sizes;
}

void CPicture::boxBlur_4(int* source, int* dest, int width, int height, int radius)
{
	memcpy(dest, source, width * height);
	boxBlurH_4(dest, source, width, height, radius);
	boxBlurT_4(source, dest, width, height, radius);
}

void CPicture::boxBlurH_4(int* source, int* dest, int width, int height, int radius)
{
	double iarr = 1.0 / (radius + radius + 1);
	for (int i = 0; i < height; ++i)
	{
		int ti = i * width;
		int li = ti;
		int ri = ti + radius;
		int fv = source[ti];
		int lv = source[ti + width - 1];
		int val = (radius + 1) * fv;
		for (int j = 0; j < radius; ++j)
		{
			val += source[ti + j];
		}
		for (int j = 0; j <= radius; ++j)
		{
			val += source[ri++] - fv;
			dest[ti++] = (int)round(val * iarr);
		}
		for (int j = radius + 1; j < width - radius; ++j)
		{
			val += source[ri++] - source[li++];
			dest[ti++] = (int)round(val * iarr);
		}
		for (int j = width - radius; j < width; j++)
		{
			val += lv - source[li++];
			dest[ti++] = (int)round(val * iarr);
		}
	}
}

void CPicture::boxBlurT_4(int* source, int* dest, int width, int height, int radius) {
	double iarr = 1.0 / (radius + radius + 1);
	for (int i = 0; i < width; ++i)
	{
		int ti = i;
		int li = ti;
		int ri = ti + radius * width;
		int fv = source[ti];
		int lv = source[ti + width * (height - 1)];
		int val = (radius + 1) * fv;
		for (int j = 0; j < radius; ++j) {
			val += source[ti + j * width];
		}
		for (int j = 0; j <= radius; ++j) {
			val += source[ri] - fv;
			dest[ti] = (int)round(val * iarr);
			ri += width;
			ti += width;
		}
		for (int j = radius + 1; j < height - radius; ++j) {
			val += source[ri] - source[li];
			dest[ti] = (int)round(val * iarr);
			li += width;
			ri += width;
			ti += width;
		}
		for (int j = height - radius; j < height; j++) {
			val += lv - source[li];
			dest[ti] = (int)round(val * iarr);
			li += width;
			ti += width;
		}
	}
}

void CPicture::gaussBlur_4(int* source, int* dest, int width, int height, int radius)
{
	int* bxs = boxesForGauss(radius, 3);
	boxBlur_4(source, dest, width, height, (bxs[0] - 1) / 2);
	boxBlur_4(dest, source, width, height, (bxs[1] - 1) / 2);
	boxBlur_4(source, dest, width, height, (bxs[2] - 1) / 2);
}