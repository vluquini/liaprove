import { http } from '@/shared/api/http'

export interface CertificateVerificationResponse {
  certificateNumber: string
  title: string
  description: string
  certificateUrl: string
  issueDate: string
  score: number
  owner: {
    id: string
    name: string
    occupation: string
    experienceLevel: 'JUNIOR' | 'PLENO' | 'SENIOR'
  }
}

export async function verifyCertificate(certificateNumber: string): Promise<CertificateVerificationResponse> {
  const response = await http.get<CertificateVerificationResponse>(`/v1/certificates/${certificateNumber}`)
  return response.data
}
